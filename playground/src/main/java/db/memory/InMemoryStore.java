package db.memory;

import db.KeyValueStore;
import db.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.emptyMap;

public class InMemoryStore implements KeyValueStore {

    private final Map<String, Table<?>> tables = new HashMap<>();

    @Override
    public <Row_Type> Table<Row_Type> createTable(String tableName, Class<Row_Type> type, Map<String, Function<Row_Type, Object>> schema, Map<String, Function<Row_Type, String>> indexes) {
        InMemoryTable<Row_Type> table = new InMemoryTable<>(tableName, schema, indexes);
        registerTable(tableName, table);
        return table;
    }

    private <Row_Type> void registerTable(String tableName, InMemoryTable<Row_Type> table) {
        tables.put(tableName, table);
    }

    @Override
    public <Row_Type> Table<Row_Type> createTable(String tableName, Class<Row_Type> type, Map<String, Function<Row_Type, Object>> schema) {
        return createTable(tableName, type, schema, emptyMap());
    }

    @Override
    public <Row_Type> Table<Row_Type> createTable(String tableName, Class<Row_Type> type, Map<String, Function<Row_Type, Object>> schema, Map<String, Function<Row_Type, String>> indexes, Function<Row_Type, byte[]> encoder, Function<byte[], Row_Type> decoder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> desc(String tableName) {
        Table<?> table = tables.get(tableName);
        return table.cols();
    }
}
