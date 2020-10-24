package db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class InMemoryKV implements KVDatabase {
    private final Map<String, Table<?>> tables = new HashMap<>();

    @Override
    public <Row_Type> Table<Row_Type> createTable(String tableName, Map<String, Function<Row_Type, Object>> cols) {
        Table<Row_Type> table = new Table<>(tableName, cols);
        tables.put(tableName, table);
        return table;
    }

    @Override
    public Collection<String> desc(String table) {
        Table<?> tableObject = tables.get(table);
        return tableObject.cols();
    }
}
