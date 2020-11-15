package db;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public interface SSTable<T_TYPE> {
    List<String> cols();

    //Search functions
    void scan(Consumer<T_TYPE> consumer, int limit);

    void search(String indexName, String searchValue, Consumer<T_TYPE> consumer, int limit);

    void search(String indexName, String searchValue, Collection<T_TYPE> container, int limit);

    void rangeSearch(String index, String startKey, String endKey, Collection<T_TYPE> container, int limit);

    T_TYPE get(String pk);

    default Map<String, Function<T_TYPE, Object>> schema() {
        return null;
    }

    default Object columnValue(String col, Object row) {
        return null;
    }

    //Mutation functions
    void insert(T_TYPE row);

    void update(T_TYPE record); // Secondary index needs rebuilding
}
