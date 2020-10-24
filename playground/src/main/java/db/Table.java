package db;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface Table<T_TYPE> {
    List<String> cols();

    void scan(Consumer<T_TYPE> consumer, int limit);

    void match(String indexName, String matchValue, int limit, Consumer<T_TYPE> consumer);

    void match(String indexName, String matchValue, int limit, Collection<T_TYPE> container);

    void insert(T_TYPE row);
}
