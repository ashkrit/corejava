package db;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Table<Row_Type> {
    public final String tableName;
    public final Map<String, Function<Row_Type, Object>> cols;


    public Table(String tableName, Map<String, Function<Row_Type, Object>> cols) {
        this.tableName = tableName;
        this.cols = cols;
    }

    public Collection<String> cols() {
        return cols
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void insert(Collection<Row_Type> rows) {

    }

    @Override
    public String toString() {
        return String.format("Table[%s]", tableName);
    }
}
