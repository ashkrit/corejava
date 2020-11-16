package query.kv;

import query.sql.SqlAPI;
import query.sql.SqlAPI.RowValue;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public interface KeyValueStore {

    <Row_Type> SSTable<Row_Type> createTable(String tableName, Class<Row_Type> type,
                                             Map<String, Function<Row_Type, Object>> schema,
                                             Map<String, Function<Row_Type, String>> indexes);

    <Row_Type> SSTable<Row_Type> createTable(String tableName, Class<Row_Type> type,
                                             Map<String, Function<Row_Type, Object>> schema);

    <Row_Type> SSTable<Row_Type> createTable(TableInfo<Row_Type> tableInfo);

    List<String> desc(String table);

    void close();

    <Row_Type> SSTable<Row_Type> table(String tableName);

    default void execute(String sql, Consumer<RowValue> consumer) {
        new SqlAPI(this).execute(sql, consumer);
    }
}
