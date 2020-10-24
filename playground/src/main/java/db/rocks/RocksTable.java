package db.rocks;

import com.google.gson.Gson;
import db.Table;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RocksTable<Row_Type> implements Table<Row_Type> {

    private final String tableName;
    private final Map<String, Function<Row_Type, String>> indexes;
    private final Map<String, Function<Row_Type, Object>> cols;
    private final Class<Row_Type> type;
    private final RocksDB db;

    public final AtomicLong id = new AtomicLong(System.nanoTime()); // Seed to keep it unique when persistence is implemented.

    public RocksTable(RocksDB db, Class<Row_Type> type, String tableName, Map<String, Function<Row_Type, String>> indexes, Map<String, Function<Row_Type, Object>> cols) {
        this.tableName = tableName;
        this.indexes = indexes;
        this.cols = cols;
        this.db = db;
        this.type = type;
    }

    @Override
    public List<String> cols() {
        return cols
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public void scan(Consumer<Row_Type> consumer, int limit) {

        String fromKey = String.format("%s/%s", tableName, "pk");
        iterate(consumer, fromKey, v -> fromJson(v), limit);

    }

    private Row_Type fromJson(String value) {
        return new Gson().fromJson(value, type);
    }

    private void iterate(Consumer<Row_Type> consumer, String fromKey, Function<String, Row_Type> converter, int limit) {
        RocksIterator itr = db.newIterator();
        itr.seek(fromKey.getBytes());
        int tracker = limit;
        while (itr.isValid() && tracker > 0) {
            String s = new String(itr.key());
            if (!s.startsWith(fromKey)) {
                break;
            }
            String json = new String(itr.value());
            consumer.accept(converter.apply(json));
            itr.next();
            tracker--;
        }
        itr.close();
    }

    @Override
    public void match(String indexName, String matchValue, int limit, Consumer<Row_Type> consumer) {
        String indexKey = buildIndexKey(indexName, matchValue);
        iterate(consumer, indexKey, v -> {
            String json = new String(get(v));
            return fromJson(json);
        }, limit);
    }

    private byte[] get(String key) {
        try {
            return db.get(key.getBytes());
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildIndexKey(String indexName, String matchValue) {
        return String.format("%s/%s/%s", tableName, indexName, matchValue);
    }

    @Override
    public void match(String indexName, String matchValue, int limit, Collection<Row_Type> container) {
        match(indexName, matchValue, limit, container::add);
    }

    @Override
    public void insert(Row_Type row) {
        long sequence = id.incrementAndGet();
        String indexKey = String.format("%s/%s/%s", tableName, "pk", sequence);
        byte[] key = indexKey.getBytes();
        put(key, new Gson().toJson(row).getBytes());
        buildIndex(row, key, indexKey);
    }

    private void put(byte[] key, byte[] value) {
        try {
            db.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void buildIndex(Row_Type row, byte[] keyRef, String key) {
        for (Map.Entry<String, Function<Row_Type, String>> index : indexes.entrySet()) {
            String indexValue = index.getValue().apply(row);
            String indexName = index.getKey();
            String indexKey = createIndexKey(key, indexValue, indexName);
            put(indexKey.getBytes(), keyRef);
        }
    }

    private String createIndexKey(String key, String indexValue, String indexName) {
        return String.format("%s/%s/%s/%s", tableName, indexName, indexValue, key);
    }
}
