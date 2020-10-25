package db.persistent.mvstore;

import db.KeyBuilder;
import db.SSTable;
import db.TableInfo;
import db.persistent.NavigablePersistentStore;
import org.h2.mvstore.MVStore;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MVStoreTable<Row_Type> implements SSTable<Row_Type> {

    private final KeyBuilder keyBuilder;
    private final NavigablePersistentStore nvStores;
    private final TableInfo<Row_Type> tableInfo;

    public MVStoreTable(MVStore store,
                        TableInfo<Row_Type> tableInfo) {
        this.tableInfo = tableInfo;
        this.keyBuilder = new KeyBuilder(tableInfo.getTableName());
        this.nvStores = new NavigableMVStores(store, tableInfo.getTableName());
    }

    @Override
    public List<String> cols() {
        return tableInfo.getSchema()
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public void scan(Consumer<Row_Type> consumer, int limit) {

        String fromKey = keyBuilder.primaryKey();
        nvStores.iterate(fromKey, v -> tableInfo.getDecoder().apply(v), consumer, limit);

    }

    @Override
    public void match(String indexName, String matchValue, Consumer<Row_Type> consumer, int limit) {
        String indexKey = keyBuilder.searchKey(indexName, matchValue);
        nvStores.iterate(indexKey, key -> tableInfo.getDecoder().apply(nvStores.get(key)), consumer, limit);
    }

    @Override
    public void match(String indexName, String matchValue, Collection<Row_Type> container, int limit) {
        match(indexName, matchValue, container::add, limit);
    }

    @Override
    public void insert(Row_Type row) {
        String sequence = tableInfo.getPk().apply(row);
        String indexKey = keyBuilder.searchKey("pk", sequence);
        byte[] key = indexKey.getBytes();
        nvStores.put(key, tableInfo.getEncoder().apply(row));
        buildIndex(row, key, indexKey);
    }

    @Override
    public void range(String index, String start, String end, List<Row_Type> returnRows, int limit) {

        String startKey = keyBuilder.searchKey(index, start);
        String endKey = keyBuilder.searchKey(index, end);

        nvStores.iterate(startKey, endKey, key -> tableInfo.getDecoder().apply(nvStores.get(key)), returnRows::add, limit);

    }

    private void buildIndex(Row_Type row, byte[] keyRef, String key) {
        for (Map.Entry<String, Function<Row_Type, String>> index : tableInfo.getIndexes().entrySet()) {
            String indexValue = index.getValue().apply(row);
            String indexName = index.getKey();
            String indexKey = keyBuilder.secondaryIndexKey(indexName, indexValue, key);
            nvStores.put(indexKey.getBytes(), keyRef); // This maintain reference to PK. To make covered full row can be stored.
        }
    }
}
