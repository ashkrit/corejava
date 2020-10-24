package db;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface KeyValueStore {

    <Row_Type> SSTable<Row_Type> createTable(String tableName, Class<Row_Type> type,
                                             Map<String, Function<Row_Type, Object>> schema,
                                             Map<String, Function<Row_Type, String>> indexes);

    <Row_Type> SSTable<Row_Type> createTable(String tableName, Class<Row_Type> type,
                                             Map<String, Function<Row_Type, Object>> schema);

    <Row_Type> SSTable<Row_Type> createTable(String tableName,
                                             Map<String, Function<Row_Type, Object>> schema,
                                             Map<String, Function<Row_Type, String>> indexes,
                                             Function<Row_Type, byte[]> encoder,
                                             Function<byte[], Row_Type> decoder);

    List<String> desc(String table);
}
