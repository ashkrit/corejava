package query.kv.persistent.mvstore;

import query.kv.KeyBuilder;
import query.kv.SSTable;
import query.kv.TableInfo;
import query.kv.persistent.*;
import org.h2.mvstore.MVStore;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MVStoreTable<Row_Type> implements SSTable<Row_Type> {

    private final KeyBuilder keyBuilder;
    private final NavigablePersistentStore nvStores;
    private final TableInfo<Row_Type> tableInfo;
    private final Set<Map.Entry<String, Function<Row_Type, String>>> indexToProcess;

    public MVStoreTable(MVStore store,
                        TableInfo<Row_Type> tableInfo) {
        this.tableInfo = tableInfo;
        this.keyBuilder = new KeyBuilder(tableInfo.getTableName());
        this.nvStores = new NavigableMVStores(store, tableInfo.getTableName());
        this.indexToProcess = tableInfo.getIndexes().entrySet();
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
    public void search(String indexName, String searchValue, Consumer<Row_Type> consumer, int limit) {
        String indexKey = keyBuilder.searchKey(indexName, searchValue);
        nvStores.iterate(indexKey, key -> tableInfo.getDecoder().apply(nvStores.get(key)), consumer, limit);
    }

    @Override
    public void search(String indexName, String searchValue, Collection<Row_Type> container, int limit) {
        search(indexName, searchValue, container::add, limit);
    }

    @Override
    public void insert(Row_Type row) {
        addRecord(row);
    }

    private void addRecord(Row_Type row) {
        String sequence = tableInfo.getPk().apply(row);
        String indexKey = keyBuilder.searchKey("pk", sequence);
        byte[] key = indexKey.getBytes();
        nvStores.put(key, tableInfo.getEncoder().apply(row));
        buildIndex(row, key, indexKey);
    }

    @Override
    public void rangeSearch(String index, String start, String end, Collection<Row_Type> container, int limit) {

        String startKey = keyBuilder.searchKey(index, start);
        String endKey = keyBuilder.searchKey(index, end);

        nvStores.iterate(startKey, endKey, key -> tableInfo.getDecoder().apply(nvStores.get(key)), container::add, limit);

    }

    @Override
    public Row_Type get(String pk) {
        String indexKey = keyBuilder.searchKey("pk", pk);
        byte[] data = nvStores.get(indexKey.getBytes());
        return tableInfo.getDecoder().apply(data);
    }

    @Override
    public void update(Row_Type record) {
        addRecord(record);
    }

    private void buildIndex(Row_Type row, byte[] keyRef, String key) {

        Stream<String> indexKeys = indexToProcess
                .stream()
                .map(index -> toIndexKey(row, key, index));

        indexKeys
                .forEach(indexKey -> nvStores.put(indexKey.getBytes(), keyRef)); //// This maintain reference to PK. To make covered full row can be stored.

    }

    private String toIndexKey(Row_Type row, String key, Map.Entry<String, Function<Row_Type, String>> index) {
        String indexValue = index.getValue().apply(row);
        String indexName = index.getKey();
        String indexKey = keyBuilder.secondaryIndexKey(indexName, indexValue, key);
        return indexKey;
    }

    @Override
    public Object columnValue(String col, Object row) {
        return tableInfo
                .getSchema()
                .get(col.toLowerCase())
                .apply((Row_Type) row);
    }

    @Override
    public Map<String, Function<Row_Type, String>> indexes() {
        return tableInfo.getIndexes();
    }
}
