package db.rocks;

import db.KeyBuilder;
import db.Table;
import org.rocksdb.RocksDB;

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
    private final Function<Row_Type, byte[]> encoder;
    private final Function<byte[], Row_Type> decoder;
    private final KeyBuilder keyBuilder;
    private final NavigableRocks nvRocks;

    public final AtomicLong id = new AtomicLong(System.nanoTime()); // Seed to keep it unique when persistence is implemented.

    public RocksTable(RocksDB db, String tableName,
                      Map<String, Function<Row_Type, String>> indexes,
                      Map<String, Function<Row_Type, Object>> cols,
                      Function<Row_Type, byte[]> encoder,
                      Function<byte[], Row_Type> decoder) {
        this.tableName = tableName;
        this.indexes = indexes;
        this.cols = cols;
        this.keyBuilder = new KeyBuilder(tableName);
        this.encoder = encoder;
        this.decoder = decoder;
        this.nvRocks = new NavigableRocks(db);
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
        nvRocks.iterate(fromKey, v -> decoder.apply(v), consumer, limit);

    }


    @Override
    public void match(String indexName, String matchValue, int limit, Consumer<Row_Type> consumer) {
        String indexKey = keyBuilder.primaryIndex(indexName, matchValue);
        nvRocks.iterate(indexKey, key -> decoder.apply(nvRocks.get(key)), consumer, limit);
    }

    @Override
    public void match(String indexName, String matchValue, int limit, Collection<Row_Type> container) {
        match(indexName, matchValue, limit, container::add);
    }

    @Override
    public void insert(Row_Type row) {
        long sequence = id.incrementAndGet();
        String indexKey = keyBuilder.primaryIndex("pk", String.valueOf(sequence));
        byte[] key = indexKey.getBytes();
        nvRocks.put(key, encoder.apply(row));
        buildIndex(row, key, indexKey);
    }

    private void buildIndex(Row_Type row, byte[] keyRef, String key) {
        for (Map.Entry<String, Function<Row_Type, String>> index : indexes.entrySet()) {
            String indexValue = index.getValue().apply(row);
            String indexName = index.getKey();
            String indexKey = keyBuilder.secondaryIndex(indexName, indexValue, key);
            nvRocks.put(indexKey.getBytes(), keyRef); // This maintain reference to PK. To make covered full row can be stored.
        }
    }

}
