package db.memory;

import db.KVDatabase;
import db.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.emptyMap;

public class InMemoryKV implements KVDatabase {

    private final Map<String, Table<?>> tables = new HashMap<>();

    @Override
    public <Row_Type> Table<Row_Type> createTable(String tableName, Map<String, Function<Row_Type, Object>> cols, Map<String, Function<Row_Type, String>> indexes) {
        InMemoryTable<Row_Type> table = new InMemoryTable<>(tableName, cols, indexes);
        registerTable(tableName, table);
        return table;
    }

    private <Row_Type> void registerTable(String tableName, InMemoryTable<Row_Type> table) {
        tables.put(tableName, table);
    }

    @Override
    public <Row_Type> Table<Row_Type> createTable(String tableName, Map<String, Function<Row_Type, Object>> cols) {
        return createTable(tableName, cols, emptyMap());
    }

    @Override
    public List<String> desc(String tableName) {
        Table<?> table = tables.get(tableName);
        return table.cols();
    }
}
