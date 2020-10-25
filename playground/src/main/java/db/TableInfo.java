package db;

import java.util.Map;
import java.util.function.Function;

public class TableInfo<Row_Type> {
    private final String tableName;
    private final Map<String, Function<Row_Type, Object>> schema;
    private final Map<String, Function<Row_Type, String>> indexes;
    private final Function<Row_Type, byte[]> encoder;
    private final Function<byte[], Row_Type> decoder;
    private final Function<Row_Type, String> pk;

    public TableInfo(String tableName,
                     Map<String, Function<Row_Type, Object>> schema,
                     Map<String, Function<Row_Type, String>> indexes,
                     Function<Row_Type, byte[]> encoder,
                     Function<byte[], Row_Type> decoder,
                     Function<Row_Type, String> pk) {
        this.tableName = tableName;
        this.schema = schema;
        this.indexes = indexes;
        this.encoder = encoder;
        this.decoder = decoder;
        this.pk = pk;
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, Function<Row_Type, Object>> getSchema() {
        return schema;
    }

    public Map<String, Function<Row_Type, String>> getIndexes() {
        return indexes;
    }

    public Function<Row_Type, byte[]> getEncoder() {
        return encoder;
    }

    public Function<byte[], Row_Type> getDecoder() {
        return decoder;
    }

    public Function<Row_Type, String> getPk() {
        return pk;
    }
}
