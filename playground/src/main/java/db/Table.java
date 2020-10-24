package db;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Table<Row_Type> {
    public final String tableName;
    public final Map<String, Function<Row_Type, Object>> cols;
    private final Map<String, Row_Type> rows = new HashMap<>();

    public Table(String tableName, Map<String, Function<Row_Type, Object>> cols) {
        this.tableName = tableName;
        this.cols = cols;
    }

    public List<String> cols() {
        return cols
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    public void scan(int limit, Consumer<Row_Type> consumer) {

        rows.entrySet().stream()
                .limit(limit)
                .map(Map.Entry::getValue)
                .forEach(consumer::accept);

    }

    public void insert(Row_Type row) {
        rows.put(UUID.randomUUID().toString(), row);
    }

    @Override
    public String toString() {
        return String.format("Table[%s]", tableName);
    }
}
