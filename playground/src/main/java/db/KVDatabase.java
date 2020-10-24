package db;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public interface KVDatabase {

    <Row_Type> Table<Row_Type> createTable(String tableName, Map<String, Function<Row_Type, Object>> cols);

    Collection<String> desc(String table);
}
