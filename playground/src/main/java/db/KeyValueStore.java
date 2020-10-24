package db;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface KeyValueStore {

    <Row_Type> Table<Row_Type> createTable(String tableName, Class<Row_Type> t, Map<String, Function<Row_Type, Object>> cols, Map<String, Function<Row_Type, String>> indexes);

    <Row_Type> Table<Row_Type> createTable(String tableName, Class<Row_Type> t, Map<String, Function<Row_Type, Object>> cols);

    List<String> desc(String table);
}
