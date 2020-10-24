package db.rocks;

import com.google.gson.Gson;
import db.Order;
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
    private final RocksDB db;

    public final AtomicLong id = new AtomicLong(System.nanoTime()); // Seed to keep it unique when persistence is implemented.

    public RocksTable(RocksDB db, String tableName, Map<String, Function<Row_Type, String>> indexes, Map<String, Function<Row_Type, Object>> cols) {
        this.tableName = tableName;
        this.indexes = indexes;
        this.cols = cols;
        this.db = db;
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
        iterate(consumer, fromKey, v -> (Row_Type) new Gson().fromJson(v, Order.class), limit);

    }

    private void iterate(Consumer<Row_Type> consumer, String fromKey, Function<String, Row_Type> fn, int limit) {
        RocksIterator itr = db.newIterator();
        itr.seek(fromKey.getBytes());
        int tracker = limit;
        while (itr.isValid() && tracker > 0) {
            String s = new String(itr.key());
            if (!s.startsWith(fromKey)) {
                break;
            }
            String json = new String(itr.value());
            consumer.accept(fn.apply(json));
            itr.next();
            tracker--;
        }
        itr.close();
    }

    @Override
    public void match(String indexName, String matchValue, int limit, Consumer<Row_Type> consumer) {
        String indexKey = buildIndexKey(indexName, matchValue);
        iterate(consumer, indexKey, v -> {
            try {
                String json = new String(db.get(v.getBytes()));
                return (Row_Type) new Gson().fromJson(json, Order.class);
            } catch (RocksDBException e) {
                throw new RuntimeException(e);
            }
        }, limit);
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
            String indexKey = String.format("%s/%s/%s/%s", tableName, indexName, indexValue, key);
            put(indexKey.getBytes(), keyRef);
        }
    }
}
