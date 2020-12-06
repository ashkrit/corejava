package query.timeseries.sst;

import java.util.Collection;
import java.util.function.Function;

public interface SortedStringTable<V> {

    void append(String key, V value);

    void iterate(String from, String to, Function<V, Boolean> consumer);

    // API for saving SST table for persistence storage
    Collection<PageRecord<V>> buffers();

    void update(int pageId, PageRecord<V> page);

    void remove(int pageId);
}
