package query.timeseries;

import model.avro.SSTablePage;

import java.util.Collection;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class SSTable<V> {

    private final AtomicReference<NavigableMap<String, V>> currentBuffer = new AtomicReference<>(new ConcurrentSkipListMap<>());
    private final NavigableMap<Integer, PageRecord<V>> readOnlyBuffer = new ConcurrentSkipListMap<>();
    private final int chunkSize;
    private final AtomicInteger currentSize = new AtomicInteger();
    private final AtomicInteger currentPage = new AtomicInteger();

    public SSTable(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public void append(String key, V value) {
        allocateNewIfFull();
        currentStore().put(key, value);
    }

    public void iterate(String from, String to, Function<V, Boolean> consumer) {
        NavigableMap<String, V> current = currentStore();
        Collection<NavigableMap<String, V>> oldValues = readOnlyBuffer
                .entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .map(PageRecord::getPageData)
                .collect(toList());

        if (from != null && to != null) {
            _iterate(current, oldValues, consumer, bt(from, to));
        } else if (from != null) {
            _iterate(current, oldValues, consumer, gt(from));
        } else if (to != null) {
            _iterate(current, oldValues, consumer, lt(to));
        }
    }

    private void allocateNewIfFull() {
        int elementCount = currentSize.incrementAndGet();

        if (isFull(elementCount)) {

            for (; isFull(currentSize.get()); ) {
                NavigableMap<String, V> old = currentStore();
                if (currentBuffer.compareAndSet(old, new ConcurrentSkipListMap<>())) {
                    closeOldPage(old);
                    break;
                } else {
                    //Lost the race. Try again but mostly it will exit loop
                }
            }
        }
    }

    private void closeOldPage(NavigableMap<String, V> old) {

        int pageId = currentPage.incrementAndGet();
        SSTablePage pageInfo = SSTablePage
                .newBuilder()
                .setPageId(pageId)
                .setMinValue(extractTime(old.firstKey()))
                .setMaxValue((extractTime(old.lastKey())))
                .setOffSet(0)//This will be based on offset
                .build();
        readOnlyBuffer.put(pageId, new PageRecord<>(old, pageInfo));
        currentSize.set(0);
    }

    private long extractTime(String s) {
        return Long.parseLong(s.substring(0, 14));
    }

    private boolean isFull(int value) {
        return value > chunkSize;
    }

    private void _iterate(NavigableMap<String, V> current, Collection<NavigableMap<String, V>> oldValues,
                          Function<V, Boolean> consumer, Function<NavigableMap<String, V>, NavigableMap<String, V>> operator) {

        if (process(consumer, operator.apply(current))) {
            for (NavigableMap<String, V> buffer : oldValues) {
                if (!process(consumer, operator.apply(buffer))) {
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


    static class PageRecord<V> {
        public final NavigableMap<String, V> pageData;
        public final SSTablePage pageInfo;

        PageRecord(NavigableMap<String, V> pageData, SSTablePage pageInfo) {
            this.pageData = pageData;
            this.pageInfo = pageInfo;
        }

        public NavigableMap<String, V> getPageData() {
            return pageData;
        }

        public SSTablePage getPageInfo() {
            return pageInfo;
        }
    }
}
