package query.timeseries.sst;

import java.util.function.Function;

public interface SortedStringTable<V> {
    void append(String key, V value);

    void iterate(String from, String to, Function<V, Boolean> consumer);
}
