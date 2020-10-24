package db.memory;

import db.KeyValueStore;
import db.SSTable;
import db.TableInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.emptyMap;

public class InMemoryStore implements KeyValueStore {

    private final Map<String, SSTable<?>> tables = new HashMap<>();

    @Override
    public <Row_Type> SSTable<Row_Type> createTable(String tableName, Class<Row_Type> type, Map<String, Function<Row_Type, Object>> schema, Map<String, Function<Row_Type, String>> indexes) {
        InMemorySSTable<Row_Type> table = new InMemorySSTable<>(tableName, schema, indexes);
        registerTable(tableName, table);
        return table;
    }

    private <Row_Type> void registerTable(String tableName, InMemorySSTable<Row_Type> table) {
        tables.put(tableName, table);
    }

    @Override
    public <Row_Type> SSTable<Row_Type> createTable(String tableName, Class<Row_Type> type, Map<String, Function<Row_Type, Object>> schema) {
        return createTable(tableName, type, schema, emptyMap());
    }

    @Override
    public <Row_Type> SSTable<Row_Type> createTable(TableInfo<Row_Type> tableInfo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> desc(String tableName) {
        SSTable<?> SSTable = tables.get(tableName);
        return SSTable.cols();
    }
}
