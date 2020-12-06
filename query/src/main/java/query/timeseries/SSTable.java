package query.timeseries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class SSTable<V> {

    private final AtomicReference<NavigableMap<String, V>> currentBuffer = new AtomicReference<>(new ConcurrentSkipListMap<>());
    private final NavigableMap<Integer, NavigableMap<String, V>> readOnlyBuffer = new ConcurrentSkipListMap<>();
    private final int chunkSize;
    private final AtomicInteger currentSize = new AtomicInteger();
    private final AtomicInteger currentPage = new AtomicInteger();

    public SSTable(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public void inset(String key, V value) {
        allocateNewIfFull();
        currentStore().put(key, value);
    }

    private void allocateNewIfFull() {
        int elementCount = currentSize.incrementAndGet();

        if (isFull(elementCount)) {

            for (; isFull(currentSize.get()); ) {
                NavigableMap<String, V> old = currentStore();
                if (currentBuffer.compareAndSet(old, new ConcurrentSkipListMap<>())) {
                    readOnlyBuffer.put(currentPage.incrementAndGet(), old);
                    currentSize.set(0);
                    break;
                } else {
                    //Lost the race. Try again but mostly it will exit loop
                }
            }
        }
    }

    public boolean isFull(int value) {
        return value > chunkSize;
    }

    public void _iterate(String from, String to, Function<V, Boolean> consumer) {
        NavigableMap<String, V> current = currentStore();
        Collection<NavigableMap<String, V>> oldValues = new ArrayList<>(readOnlyBuffer.descendingMap().values());

        if (from != null && to != null) {
            _iterate(current, oldValues, consumer, bt(from, to));
        } else if (from != null) {
            _iterate(current, oldValues, consumer, gt(from));
        } else if (to != null) {
            _iterate(current, oldValues, consumer, lt(to));
        }
    }

    private void _iterate(NavigableMap<String, V> current, Collection<NavigableMap<String, V>> oldValues, Function<V, Boolean> consumer, Function<NavigableMap<String, V>, NavigableMap<String, V>> filter) {

        if (process(consumer, filter.apply(current))) {
            for (NavigableMap<String, V> buffer : oldValues) {
                if (!process(consumer, filter.apply(buffer))) {
                    break;
                }
            }
        }
    }

    public boolean process(Function<V, Boolean> fn, NavigableMap<String, V> matched) {
        for (Map.Entry<String, V> e : matched.entrySet()) {
            if (!fn.apply(e.getValue())) {
                return false;
            }
        }
        return true;
    }


    private NavigableMap<String, V> currentStore() {
        return currentBuffer.get();
    }

    private Function<NavigableMap<String, V>, NavigableMap<String, V>> bt(String from, String to) {
        return i -> i.subMap(from, true, to, true);
    }

    private Function<NavigableMap<String, V>, NavigableMap<String, V>> lt(String to) {
        return i -> i.headMap(to, true);
    }

    private Function<NavigableMap<String, V>, NavigableMap<String, V>> gt(String from) {
        return i -> i.tailMap(from, true);
    }
}
