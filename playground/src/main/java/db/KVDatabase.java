package db;

import db.tables.Order;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface KVDatabase {

    <Row_Type> Table<Row_Type> createTable(String tableName, Map<String, Function<Row_Type, Object>> cols, Map<String, Function<Row_Type, String>> indexes);

    <Row_Type> Table<Row_Type> createTable(String tableName, Map<String, Function<Row_Type, Object>> cols);

    List<String> desc(String table);
}
