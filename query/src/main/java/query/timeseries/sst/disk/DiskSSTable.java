package query.timeseries.sst.disk;

import query.timeseries.sst.PageRecord;
import query.timeseries.sst.SortedStringTable;

import java.util.Collection;
import java.util.function.Function;

public class DiskSSTable<V> implements SortedStringTable<V> {

    private final SortedStringTable<V> underlyingStore;

    public DiskSSTable(SortedStringTable<V> underlyingStore) {
        this.underlyingStore = underlyingStore;
    }

    @Override
    public void append(String key, V value) {
        underlyingStore.append(key, value);
    }

    @Override
    public void iterate(String from, String to, Function<V, Boolean> consumer) {
        underlyingStore.iterate(from, to, consumer);
    }

    @Override
    public Collection<PageRecord<V>> buffers() {
        return underlyingStore.buffers();
    }

    @Override
    public void update(int pageId, PageRecord<V> page) {
        throw new IllegalArgumentException("Not supported");
    }

    @Override
    public void remove(int pageId) {
        throw new IllegalArgumentException("Not supported");
    }

    @Override
    public void flush() {

    }
}
