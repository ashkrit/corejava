package db.persistent.mvstore;

import db.KeyBuilder;
import db.persistent.NavigablePersistentStore;
import db.SSTable;
import org.h2.mvstore.MVStore;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MVStoreTable<Row_Type> implements SSTable<Row_Type> {

    private final String tableName;
    private final Map<String, Function<Row_Type, String>> indexes;
    private final Map<String, Function<Row_Type, Object>> cols;

    private final Function<Row_Type, byte[]> encoder;
    private final Function<byte[], Row_Type> decoder;

    private final KeyBuilder keyBuilder;

    public final AtomicLong id = new AtomicLong(System.nanoTime()); // Seed to keep it unique when persistence is implemented.
    private final NavigablePersistentStore nvStores;

    public MVStoreTable(MVStore store, String tableName, Map<String, Function<Row_Type, String>> indexes, Map<String, Function<Row_Type, Object>> cols, Function<Row_Type, byte[]> encoder, Function<byte[], Row_Type> decoder) {
        this.tableName = tableName;
        this.indexes = indexes;
        this.cols = cols;
        this.encoder = encoder;
        this.decoder = decoder;
        this.keyBuilder = new KeyBuilder(tableName);
        this.nvStores = new NavigableMVStores(store, tableName);
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

        String fromKey = keyBuilder.primaryKey();
        nvStores.iterate(fromKey, v -> decoder.apply(v), consumer, limit);

    }

    @Override
    public void match(String indexName, String matchValue, Consumer<Row_Type> consumer, int limit) {
        String indexKey = keyBuilder.searchKey(indexName, matchValue);
        nvStores.iterate(indexKey, key -> decoder.apply(nvStores.get(key)), consumer, limit);
    }

    @Override
    public void match(String indexName, String matchValue, Collection<Row_Type> container, int limit) {
        match(indexName, matchValue, container::add, limit);
    }

    @Override
    public void insert(Row_Type row) {
        long sequence = id.incrementAndGet();
        String indexKey = keyBuilder.searchKey("pk", String.valueOf(sequence));
        byte[] key = indexKey.getBytes();
        nvStores.put(key, encoder.apply(row));
        buildIndex(row, key, indexKey);
    }

    private void buildIndex(Row_Type row, byte[] keyRef, String key) {
        for (Map.Entry<String, Function<Row_Type, String>> index : indexes.entrySet()) {
            String indexValue = index.getValue().apply(row);
            String indexName = index.getKey();
            String indexKey = keyBuilder.secondaryIndexKey(indexName, indexValue, key);
            nvStores.put(indexKey.getBytes(), keyRef); // This maintain reference to PK. To make covered full row can be stored.
        }
    }
}
