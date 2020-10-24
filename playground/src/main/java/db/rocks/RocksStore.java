package db.rocks;

import com.google.gson.Gson;
import db.KeyValueStore;
import db.Table;
import org.rocksdb.RocksDB;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.emptyMap;

public class RocksStore implements KeyValueStore {
    private final Map<String, Table<?>> tables = new HashMap<>();
    private final RocksDB rocksDB;

    public RocksStore(File rootFolder) {
        this.rocksDB = RocksDBDriver.openDatabase(rootFolder);
    }

    @Override
    public <Row_Type> Table<Row_Type> createTable(String tableName, Class<Row_Type> type, Map<String, Function<Row_Type, Object>> schema, Map<String, Function<Row_Type, String>> indexes) {
        return createTable(tableName, type, schema, indexes, row -> new Gson().toJson(row).getBytes(), rawBytes -> new Gson().fromJson(new String(rawBytes), type));
    }

    private <Row_Type> void registerTable(String tableName, Table<Row_Type> table) {
        tables.put(tableName, table);
    }

    @Override
    public <Row_Type> Table<Row_Type> createTable(String tableName, Class<Row_Type> type, Map<String, Function<Row_Type, Object>> schema) {
        return createTable(tableName, type, schema, emptyMap());
    }

    @Override
    public <Row_Type> Table<Row_Type> createTable(String tableName, Class<Row_Type> type, Map<String, Function<Row_Type, Object>> schema, Map<String, Function<Row_Type, String>> indexes,
                                                  Function<Row_Type, byte[]> encoder, Function<byte[], Row_Type> decoder) {
        Table<Row_Type> table = new RocksTable<>(rocksDB, tableName, indexes, schema, encoder, decoder);
        registerTable(tableName, table);
        return table;
    }

    @Override
    public List<String> desc(String tableName) {
        Table<?> table = tables.get(tableName);
        return table.cols();
    }

    public void close() {
        this.rocksDB.close();
    }
}
