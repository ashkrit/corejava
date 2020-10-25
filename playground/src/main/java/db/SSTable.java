package db;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface SSTable<T_TYPE> {
    List<String> cols();

    void scan(Consumer<T_TYPE> consumer, int limit);

    void match(String indexName, String matchValue, Consumer<T_TYPE> consumer, int limit);

    void match(String indexName, String matchValue, Collection<T_TYPE> container, int limit);

    void insert(T_TYPE row);

    void range(String index, String start, String end, List<T_TYPE> returnRows, int limit);
}
