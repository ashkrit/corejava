package db.persistent.mvstore;

import com.google.gson.Gson;
import db.KeyValueStore;
import db.SSTable;
import db.TableInfo;
import org.h2.mvstore.MVStore;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static java.util.Collections.emptyMap;

public class H2MVStore implements KeyValueStore {

    private final Map<String, SSTable<?>> tables = new HashMap<>();
    private final MVStore mvStore;

    public H2MVStore(File rootFolder) {
        this.mvStore = MVStore.open(rootFolder.getAbsolutePath());
    }

    @Override
    public <Row_Type> SSTable<Row_Type> createTable(String tableName, Class<Row_Type> type, Map<String, Function<Row_Type, Object>> schema, Map<String, Function<Row_Type, String>> indexes) {
        Function<Row_Type, byte[]> toJson = row -> new Gson().toJson(row).getBytes();
        Function<byte[], Row_Type> toRecord = rawBytes -> new Gson().fromJson(new String(rawBytes), type);
        AtomicLong auto = new AtomicLong(System.nanoTime());
        return createTable(new TableInfo<>(tableName, schema, indexes, toJson, toRecord, $ -> String.valueOf(auto.incrementAndGet())));
    }

    private <Row_Type> void registerTable(String tableName, SSTable<Row_Type> SSTable) {
        tables.put(tableName, SSTable);
    }

    @Override
    public <Row_Type> SSTable<Row_Type> createTable(String tableName, Class<Row_Type> type, Map<String, Function<Row_Type, Object>> schema) {
        return createTable(tableName, type, schema, emptyMap());
    }

    @Override
    public <Row_Type> SSTable<Row_Type> createTable(TableInfo<Row_Type> tableInfo) {
        SSTable<Row_Type> SSTable = new MVStoreTable<>(mvStore, tableInfo);
        registerTable(tableInfo.getTableName(), SSTable);
        return SSTable;
    }

    @Override
    public List<String> desc(String tableName) {
        SSTable<?> SSTable = tables.get(tableName);
        return SSTable.cols();
    }

    public void close() {
        this.mvStore.compactFile(1000);
        this.mvStore.close();
    }
}
