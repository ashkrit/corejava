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

    private AtomicInteger currentSize = new AtomicInteger();
    private AtomicInteger currentPage = new AtomicInteger();
    private final int chunkSize;


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
                    //Lost the race. Try again
                }
            }
        }
    }

    public boolean isFull(int value) {
        return value > chunkSize;
    }

    public void iterate(String from, String to, Function<V, Boolean> consumer) {
        NavigableMap<String, V> current = currentStore();
        Collection<NavigableMap<String, V>> oldValues = new ArrayList<>(readOnlyBuffer.descendingMap().values());
        if (from != null && to != null) {
            boolean c = process(consumer, current.subMap(from, true, to, true));
            if (c) {
                for (NavigableMap<String, V> buffer : oldValues) {
                    if (!process(consumer, buffer.subMap(from, true, to, true))) {
                        break;
                    }
                }
            }

        } else if (from != null) {
            boolean c = process(consumer, current.tailMap(from, true));
            if (c) {
                for (NavigableMap<String, V> buffer : oldValues) {
                    if (!process(consumer, buffer.tailMap(from, true))) {
                        break;
                    }
                }
            }
        } else if (to != null) {
            boolean c = process(consumer, current.headMap(to, true));
            if (c) {
                for (NavigableMap<String, V> buffer : oldValues) {
                    if (!process(consumer, buffer.headMap(to, true))) {
                        break;
                    }
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

}
