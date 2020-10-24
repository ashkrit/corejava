package db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.emptyMap;

public class InMemoryKV implements KVDatabase {
    private final Map<String, Table<?>> tables = new HashMap<>();

    @Override
    public <Row_Type> Table<Row_Type> createTable(String tableName, Map<String, Function<Row_Type, Object>> cols, Map<String, Function<Row_Type, String>> indexes) {
        Table<Row_Type> table = new Table<>(tableName, cols, indexes);

        tables.put(tableName, table);

        return table;
    }

    @Override
    public <Row_Type> Table<Row_Type> createTable(String tableName, Map<String, Function<Row_Type, Object>> cols) {
        return createTable(tableName, cols, emptyMap());
    }

    @Override
    public List<String> desc(String table) {
        Table<?> tableObject = tables.get(table);
        return tableObject.cols();
    }
}
