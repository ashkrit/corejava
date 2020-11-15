package query;

import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOrderBy;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformanceEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SqlAPI {

    private final KeyValueStore db;

    public SqlAPI(KeyValueStore keyValueStore) {
        this.db = keyValueStore;
    }

    void execute(String sql, Consumer<RowValue> consumer) {
        SqlParser.Config config = SqlParser.config()
                .withConformance(SqlConformanceEnum.MYSQL_5);

        SqlParser p = SqlParser.create(sql, config);
        try {
            SqlNode sqlNode = p.parseQuery();
            if (sqlNode instanceof SqlSelect) {
                fullTableScan(db, consumer, (SqlSelect) sqlNode, 10);
            } else if (sqlNode instanceof SqlOrderBy) {
                SqlOrderBy order = (SqlOrderBy) sqlNode;
                SqlSelect node = (SqlSelect) order.query;
                fullTableScan(db, consumer, node, Integer.parseInt(order.fetch.toString()));
            } else {
                throw new RuntimeException(sqlNode.getClass() + " not supported");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    void fullTableScan(KeyValueStore db, Consumer<RowValue> consumer, SqlSelect sqlNode, int limit) {
        SqlSelect node = sqlNode;
        String from = node.getFrom().toString();
        SSTable<?> tableObject = db.table(from.toLowerCase());

        Map<String, Integer> nameToIndex = new HashMap<>();
        int index = 0;
        for (String n : tableObject.cols()) {
            nameToIndex.put(n, index++);
        }
        RowValue row = new RowValue(tableObject, nameToIndex);
        if (node.getWhere() == null) {

            tableObject.scan(r -> {
                row.r = r;
                consumer.accept(row);
            }, limit);
        }
    }

    public class RowValue {


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
