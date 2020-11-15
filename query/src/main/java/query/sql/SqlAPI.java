package query.sql;

import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import query.kv.KeyValueStore;
import query.kv.SSTable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
                row.internalRow = r;
                consumer.accept(row);
            }, limit);
        } else {
            SqlBasicCall where = (SqlBasicCall) node.getWhere();
            Predicate<Object> base = ($) -> true;
            Predicate<Object> matcher = predicate(base, where, tableObject);
            tableObject.scan(r -> {
                if (matcher.test(r)) {
                    row.internalRow = r;
                    consumer.accept(row);
                }

            }, limit);

        }
    }


    private Predicate<Object> predicate(Predicate<Object> base, SqlBasicCall where, SSTable<?> tableObject) {
        SqlOperator operator = where.getOperator();

        String name = operator.getName().toLowerCase();

        switch (name) {
            case "=": {
                String columnName = ((SqlIdentifier) where.operands[0]).names.get(0).toLowerCase();
                String columnValue = ((SqlLiteral) where.operands[1]).toValue();
                Predicate<Object> matcher = createEq(tableObject, columnValue, columnName);
                return matcher;
            }
            case "and": {
                SqlBasicCall left = (SqlBasicCall) where.operands[0];
                SqlBasicCall right = (SqlBasicCall) where.operands[1];
                Predicate<Object> combineCondition = predicate(base, left, tableObject).and(predicate(base, right, tableObject));
                return base.and(combineCondition);
            }
            case "or": {
                SqlBasicCall left = (SqlBasicCall) where.operands[0];
                SqlBasicCall right = (SqlBasicCall) where.operands[1];
                Predicate<Object> combineCondition = predicate(base, left, tableObject).or(predicate(base, right, tableObject));
                return base.and(combineCondition);
            }
        }

        throw new RuntimeException(operator + " not supported ");
    }

    private Predicate<Object> createEq(SSTable<?> tableObject, String columnValue, String columnName) {
        Predicate<Object> eq = row -> {
            String value = tableObject.columnValue(columnName, row).toString();
            return value.equals(columnValue);
        };
        return eq;
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
        private Object internalRow;

        public RowValue(SSTable<?> tableObject, Map<String, Integer> nameToIndex) {
            this.nameToIndex = nameToIndex;
            this.tableObject = tableObject;
        }

        public Object getValue(String name) {
            return tableObject.columnValue(name, internalRow);
        }

        public long getLong(String name) {
            return (Long) tableObject.columnValue(name, internalRow);
        }

        public String getString(String index) {
            return (String) tableObject.columnValue(index, internalRow);
        }

        public int getInt(String index) {
            return (Integer) tableObject.columnValue(index, internalRow);
        }

        public double getDouble(String index) {
            return (Double) tableObject.columnValue(index, internalRow);
        }

    }
}
