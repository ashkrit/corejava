package db.memory;

import db.SSTable;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemorySSTable<Row_Type> implements SSTable<Row_Type> {

    private final String tableName;
    private final Map<String, Function<Row_Type, String>> indexes;
    private final Map<String, Function<Row_Type, Object>> cols;

    public final AtomicLong id = new AtomicLong(System.nanoTime()); // Seed to keep it unique when persistence is implemented.

    private final Map<Long, Row_Type> rawRows = new HashMap<>();
    private final NavigableMap<String, Long> indexRows = new TreeMap<>();

    public InMemorySSTable(String tableName, Map<String, Function<Row_Type, Object>> cols, Map<String, Function<Row_Type, String>> indexes) {
        this.tableName = tableName;
        this.cols = cols;
        this.indexes = indexes;
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
        rawRows.entrySet().stream()
                .limit(limit)
                .map(Map.Entry::getValue)
                .forEach(consumer::accept);

    }

    @Override
    public void match(String indexName, String matchValue, Consumer<Row_Type> consumer, int limit) {
        String indexKey = buildIndexKey(indexName, matchValue);
        Stream<Row_Type> rows = rows(indexKey, limit);
        rows.forEach(consumer::accept);
    }

    @Override
    public void match(String indexName, String matchValue, Collection<Row_Type> container, int limit) {
        match(indexName, matchValue, container::add, limit);
    }

    private Stream<Row_Type> rows(String indexKey, int limit) {
        Stream<Map.Entry<String, Long>> filterRows = indexRows
                .tailMap(indexKey).entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(indexKey));

        Stream<Row_Type> rows = filterRows
                .limit(limit)
                .map(Map.Entry::getValue)
                .map(rawRows::get);

        return rows;
    }

    private String buildIndexKey(String indexName, String matchValue) {
        return String.format("%s/%s/%s", tableName, indexName, matchValue);
    }

    @Override
    public void insert(Row_Type row) {
        long key = id.incrementAndGet();
        rawRows.put(key, row);
        buildIndex(row, key);
    }

    @Override
    public void range(String index, String start, String end, List<Row_Type> returnRows, int limit) {
        String startKey = buildIndexKey(index, start);
        String endKey = buildIndexKey(index, end);
        Stream<Row_Type> rows = rows(startKey, endKey, limit);
        rows.forEach(returnRows::add);

    }

    private Stream<Row_Type> rows(String startKey, String endKey, int limit) {
        Stream<Map.Entry<String, Long>> filterRows = indexRows
                .subMap(startKey, true, endKey, true)
                .entrySet()
                .stream();

        Stream<Row_Type> rows = filterRows
                .limit(limit)
                .map(Map.Entry::getValue)
                .map(rawRows::get);

        return rows;
    }

    private void buildIndex(Row_Type row, long key) {
        for (Map.Entry<String, Function<Row_Type, String>> index : indexes.entrySet()) {
            String indexValue = index.getValue().apply(row);
            String indexName = index.getKey();
            String indexKey = String.format("%s/%s/%s/%s", tableName, indexName, indexValue, key);
            indexRows.put(indexKey, key);
        }
    }

    @Override
    public String toString() {
        return String.format("Table[%s]", tableName);
    }
}
