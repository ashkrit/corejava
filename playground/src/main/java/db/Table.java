package db;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table<Row_Type> {
    public final String tableName;
    private final Map<String, Function<Row_Type, String>> indexes;
    public final Map<String, Function<Row_Type, Object>> cols;

    public final AtomicLong id = new AtomicLong(System.nanoTime());

    private final Map<Long, Row_Type> rawRows = new HashMap<>();
    private final NavigableMap<String, Long> indexRows = new TreeMap<>();


    public Table(String tableName, Map<String, Function<Row_Type, Object>> cols, Map<String, Function<Row_Type, String>> indexes) {
        this.tableName = tableName;
        this.cols = cols;
        this.indexes = indexes;
    }

    public List<String> cols() {
        return cols
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    public void scan(int limit, Consumer<Row_Type> consumer) {
        rawRows.entrySet().stream()
                .limit(limit)
                .map(Map.Entry::getValue)
                .forEach(consumer::accept);

    }

    public void match(String indexName, String matchValue, int limit, Consumer<Row_Type> consumer) {
        String indexKey = String.format("%s/%s/%s", tableName, indexName, matchValue);

        Stream<Map.Entry<String, Long>> filterRows = indexRows.tailMap(indexKey).entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(indexKey));

        Stream<Row_Type> rows = filterRows
                .limit(limit)
                .map(Map.Entry::getValue)
                .map(rawRows::get);

        rows.forEach(consumer::accept);

    }

    public void insert(Row_Type row) {
        long key = id.incrementAndGet();
        rawRows.put(key, row);
        buildIndex(row, key);
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
