package query.kv.memory;

import query.kv.KeyValueStore;
import query.kv.SSTable;
import query.kv.TableInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static java.util.Collections.emptyMap;

public class InMemoryStore implements KeyValueStore {

    public static String type = "memory:";
    private final Map<String, SSTable<?>> tables = new HashMap<>();

    @Override
    public <Row_Type> SSTable<Row_Type> createTable(String tableName, Class<Row_Type> type, Map<String, Function<Row_Type, Object>> schema, Map<String, Function<Row_Type, String>> indexes) {
        AtomicLong l = new AtomicLong(System.nanoTime());
        TableInfo<Row_Type> tableInfo = new TableInfo<>(tableName, schema, indexes, null, null, $ -> String.valueOf(l.incrementAndGet()));
        return createTable(tableInfo);
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
        InMemorySSTable<Row_Type> table = new InMemorySSTable<>(tableInfo);
        registerTable(tableInfo.getTableName(), table);
        return table;
    }

    @Override
    public List<String> desc(String tableName) {
        SSTable<?> SSTable = tables.get(tableName);
        return SSTable.cols();
    }

    @Override
    public void close() {

    }

    @Override
    public <Row_Type> SSTable<Row_Type> table(String tableName) {
        return (SSTable<Row_Type>) tables.get(tableName);
    }

}
