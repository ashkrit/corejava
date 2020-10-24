package db.rocks;

import db.Table;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static db.rocks.RocksDBDriver.get;
import static db.rocks.RocksDBDriver.put;

public class RocksTable<Row_Type> implements Table<Row_Type> {

    private final String tableName;
    private final Map<String, Function<Row_Type, String>> indexes;
    private final Map<String, Function<Row_Type, Object>> cols;
    private final Function<Row_Type, byte[]> encoder;
    private final Function<byte[], Row_Type> decoder;
    private final KeyBuilder keyBuilder;
    private final RocksDB db;

    public final AtomicLong id = new AtomicLong(System.nanoTime()); // Seed to keep it unique when persistence is implemented.

    public RocksTable(RocksDB db, String tableName,
                      Map<String, Function<Row_Type, String>> indexes,
                      Map<String, Function<Row_Type, Object>> cols,
                      Function<Row_Type, byte[]> encoder,
                      Function<byte[], Row_Type> decoder) {
        this.tableName = tableName;
        this.indexes = indexes;
        this.cols = cols;
        this.db = db;
        this.keyBuilder = new KeyBuilder(tableName);
        this.encoder = encoder;
        this.decoder = decoder;
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

        String fromKey = keyBuilder.primaryIndex();
        iterate(consumer, fromKey, v -> decoder.apply(v), limit);

    }

    private void iterate(Consumer<Row_Type> consumer, String fromKey, Function<byte[], Row_Type> converter, int limit) {
        RocksIterator itr = db.newIterator();
        itr.seek(fromKey.getBytes());
        int tracker = limit;
        while (itr.isValid() && tracker > 0) {
            String s = new String(itr.key());
            if (!s.startsWith(fromKey)) {
                break;
            }
            consumer.accept(converter.apply(itr.value()));
            itr.next();
            tracker--;
        }
        itr.close();
    }

    @Override
    public void match(String indexName, String matchValue, int limit, Consumer<Row_Type> consumer) {
        String indexKey = keyBuilder.searchKey(indexName, matchValue);
        iterate(consumer, indexKey, key -> {
            byte[] rawBytes = get(db, key);
            return decoder.apply(rawBytes);
        }, limit);
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
        put(db, key, encoder.apply(row));
        buildIndex(row, key, indexKey);
    }

    private void buildIndex(Row_Type row, byte[] keyRef, String key) {
        for (Map.Entry<String, Function<Row_Type, String>> index : indexes.entrySet()) {
            String indexValue = index.getValue().apply(row);
            String indexName = index.getKey();
            String indexKey = keyBuilder.rowKey(indexName, indexValue, key);
            put(db, indexKey.getBytes(), keyRef);
        }
    }

    static class KeyBuilder {
        final String tableName;

        /*
          Format
          table/index/indexvalue/rowid
         */
        KeyBuilder(String tableName) {
            this.tableName = tableName;
        }

        public String rowKey(String indexName, String indexValue, String key) {
            return String.format("%s/%s/%s/%s", tableName, indexName, indexValue, key);
        }

        public String searchKey(String indexName, String indexValue) {
            return String.format("%s/%s/%s", tableName, indexName, indexValue);
        }

        public String primaryIndex() {
            return String.format("%s/%s", tableName, "pk");
        }
    }
}
