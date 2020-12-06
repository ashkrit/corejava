package query.timeseries.sst;

import java.util.Collection;
import java.util.function.Function;

public class DiskSSTable<V> implements SortedStringTable<V> {
    private final SortedStringTable<V> underlyingTable;

    public DiskSSTable(SortedStringTable<V> underlyingTable) {
        this.underlyingTable = underlyingTable;
    }

    @Override
    public void append(String key, V value) {
        underlyingTable.append(key, value);
    }

    @Override
    public void iterate(String from, String to, Function<V, Boolean> consumer) {
        underlyingTable.iterate(from, to, consumer);
    }

    @Override
    public Collection<PageRecord<V>> buffers() {
        throw new IllegalArgumentException("Not supported");
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
        //This function will DO IO
    }

}
