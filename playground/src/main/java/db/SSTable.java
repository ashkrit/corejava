package db;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface SSTable<T_TYPE> {
    List<String> cols();

    //Search functions
    void scan(Consumer<T_TYPE> consumer, int limit);

    void match(String indexName, String matchValue, Consumer<T_TYPE> consumer, int limit);

    void match(String indexName, String matchValue, Collection<T_TYPE> container, int limit);

    void range(String index, String start, String end, Collection<T_TYPE> container, int limit);

    T_TYPE get(String pk);

    //Mutation functions
    void insert(T_TYPE row);

    void update(T_TYPE record); // Secondary index needs rebuilding
}
