package db;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface SSTable<T_TYPE> {
    List<String> cols();

    //Search functions
    void scan(Consumer<T_TYPE> consumer, int limit);

    void search(String indexName, String searchValue, Consumer<T_TYPE> consumer, int limit);

    void search(String indexName, String searchValue, Collection<T_TYPE> container, int limit);

    void rangeSearch(String index, String startKey, String endKey, Collection<T_TYPE> container, int limit);

    T_TYPE get(String pk);

    //Mutation functions
    void insert(T_TYPE row);

    void update(T_TYPE record); // Secondary index needs rebuilding
}
