package db;

import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface KeyValueStore {

    <Row_Type> SSTable<Row_Type> createTable(String tableName, Class<Row_Type> type,
                                             Map<String, Function<Row_Type, Object>> schema,
                                             Map<String, Function<Row_Type, String>> indexes);

    <Row_Type> SSTable<Row_Type> createTable(String tableName, Class<Row_Type> type,
                                             Map<String, Function<Row_Type, Object>> schema);

    <Row_Type> SSTable<Row_Type> createTable(TableInfo<Row_Type> tableInfo);

    List<String> desc(String table);

    void close();

    default <Row_Type> SSTable<Row_Type> table(String tableName) {
        return null;
    }

    default void execute(String sql, Consumer<RowValue> consumer) {
        SqlParser p = SqlParser.create(sql);
        try {
            SqlSelect node = (SqlSelect) p.parseQuery();
            String from = node.getFrom().toString();
            SSTable<?> tableObject = table(from.toLowerCase());

            Map<String, Integer> nameToIndex = new HashMap<>();
            int index = 0;
            for (String n : tableObject.cols()) {
                nameToIndex.put(n, index++);
            }
            String[] columnsToLoad = tableObject.cols().toArray(new String[]{});
            int noOfColumns = columnsToLoad.length;
            RowValue row = new RowValue(tableObject, nameToIndex);


            if (node.getWhere() == null) {
                tableObject.scan(r -> {
                    row.r = r;
                    consumer.accept(row);
                }, 5);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    class RowValue {


        private final Map<String, Integer> nameToIndex;
        private final SSTable<?> tableObject;
        private Object r;

        public RowValue(SSTable<?> tableObject, Map<String, Integer> nameToIndex) {
            this.nameToIndex = nameToIndex;
            this.tableObject = tableObject;
        }

        public Object getValue(String name) {
            return tableObject.columnValue(name, r);
        }

        public long getLong(String name) {
            return (Long) tableObject.columnValue(name, r);
        }

        public String getString(String index) {
            return (String) tableObject.columnValue(index, r);
        }

        public int getInt(String index) {
            return (Integer) tableObject.columnValue(index, r);
        }

        public double getDouble(String index) {
            return (Double) tableObject.columnValue(index, r);
        }

    }
}
