package db.persistent.rocks;

import com.google.gson.Gson;
import db.KeyValueStore;
import db.SSTable;
import db.TableInfo;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static java.util.Collections.emptyMap;

public class RocksStore implements KeyValueStore {
    private final Map<String, SSTable<?>> tables = new HashMap<>();
    private final RocksDB rocksDB;

    public RocksStore(File rootFolder) {
        this.rocksDB = RocksDBDriver.openDatabase(rootFolder);
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
        SSTable<Row_Type> SSTable = new RocksTable<>(rocksDB, tableInfo);
        registerTable(tableInfo.getTableName(), SSTable);
        return SSTable;
    }

    @Override
    public List<String> desc(String tableName) {
        SSTable<?> SSTable = tables.get(tableName);
        return SSTable.cols();
    }

    public void close() {
        try {
            this.rocksDB.compactRange();
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
        this.rocksDB.close();
    }
}
