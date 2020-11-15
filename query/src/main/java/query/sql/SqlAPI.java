package query.sql;

import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOrderBy;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import query.kv.KeyValueStore;
import query.kv.SSTable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SqlAPI {

    private final KeyValueStore db;
    private final SqlParser.Config config = SqlParser
            .config()
            .withConformance(SqlConformanceEnum.MYSQL_5);

    public SqlAPI(KeyValueStore keyValueStore) {
        this.db = keyValueStore;
    }

    public void execute(String sql, Consumer<RowValue> consumer) {

        SqlNode sqlNode = parseQuery(sql);
        if (isSimpleSelect(sqlNode)) {
            scan(consumer, (SqlSelect) sqlNode, 10);
        } else if (isOrderBy(sqlNode)) {
            SqlOrderBy order = (SqlOrderBy) sqlNode;
            SqlSelect node = (SqlSelect) order.query;
            scan(consumer, node, Integer.parseInt(order.fetch.toString()));
        } else {
            throw new RuntimeException(sqlNode.getClass() + " not supported");
        }

    }

    public boolean isOrderBy(SqlNode sqlNode) {
        return sqlNode instanceof SqlOrderBy;
    }

    public boolean isSimpleSelect(SqlNode sqlNode) {
        return sqlNode instanceof SqlSelect;
    }

    public SqlNode parseQuery(String sql) {
        try {
            SqlParser sqlParser = SqlParser.create(sql, config);
            return sqlParser.parseQuery();
        } catch (SqlParseException e) {
            throw new RuntimeException(e);
        }
    }


    void scan(Consumer<RowValue> consumer, SqlSelect node, int limit) {
        String from = node.getFrom().toString();
        SSTable<?> tableObject = db.table(from.toLowerCase());
        Map<String, Integer> nameToIndex = columnOffSet(tableObject);

        RowValue row = new RowValue(tableObject, nameToIndex);

        if (hasNoFilter(node)) {
            tableObject.scan(r -> {
                row.r = r;
                consumer.accept(row);
            }, limit);
        }
    }

    private boolean hasNoFilter(SqlSelect node) {
        return node.getWhere() == null;
    }

    private Map<String, Integer> columnOffSet(SSTable<?> tableObject) {
        Map<String, Integer> nameToIndex = new HashMap<>();
        int index = 0;
        for (String n : tableObject.cols()) {
            nameToIndex.put(n, index++);
        }
        return nameToIndex;
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
