package db;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface KVDatabase {

    <Row_Type> Table<Row_Type> createTable(String tableName, Map<String, Function<Row_Type, Object>> cols);

    List<String> desc(String table);
}
