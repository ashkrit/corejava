package query.sql;

import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import query.kv.KeyValueStore;
import query.kv.SSTable;
import query.sql.RecordFilterInfo.IndexParameter;

import java.util.HashMap;
import java.util.HashSet;
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
        SSTable<?> table = db.table(from.toLowerCase());
        Map<String, Integer> nameToIndex = columnOffSet(table);

        RowValue rowContainer = new RowValue(table, nameToIndex);
        RecordFilterInfo filterInfo = createMatcher(node, table);

        System.out.println("Possible index" + filterInfo.indexes);
        if (filterInfo.indexes.isEmpty()) {
            table.scan(currentRow -> match(consumer, rowContainer, filterInfo.predicate, currentRow), limit);
        } else {
            IndexParameter index = filterInfo.indexes.iterator().next();
            System.out.println("Using index " + index);
            table.search(index.indexName, index.indexValue, currentRow -> match(consumer, rowContainer, filterInfo.predicate, currentRow), limit);
        }
    }

    private void match(Consumer<RowValue> consumer, RowValue row, Predicate<Object> matcher, Object r) {
        if (matcher.test(r)) {
            row.internalRow = r;
            consumer.accept(row);
        }
    }

    private RecordFilterInfo createMatcher(SqlSelect node, SSTable<?> table) {

        if (hasNoFilter(node)) {
            return new RecordFilterInfo($ -> true, new HashSet<>());
        } else {
            SqlBasicCall where = (SqlBasicCall) node.getWhere();
            HashSet<IndexParameter> indexes = new HashSet<>();
            return new RecordFilterInfo(predicate(where, table, indexes), indexes);
        }
    }


    private Predicate<Object> predicate(SqlBasicCall where, SSTable<?> table, HashSet<IndexParameter> indexes) {
        SqlOperator operator = where.getOperator();

        String name = operator.getName().toLowerCase();

        switch (name) {
            case "=": {
                SqlIdentifier filterColumn = (SqlIdentifier) where.operands[0];
                SqlLiteral filterValue = (SqlLiteral) where.operands[1];

                String columnName = filterColumn.names.get(0).toLowerCase();
                String columnValue = filterValue.toValue();

                System.out.println("Index " + table.indexes().containsKey(columnName));
                if (table.indexes().containsKey(columnName)) {
                    indexes.add(new IndexParameter(columnName, columnValue));
                }

                Predicate<Object> matcher = createEq(table, columnValue, columnName);
                return matcher;
            }
            case "and": {
                SqlBasicCall left = (SqlBasicCall) where.operands[0];
                SqlBasicCall right = (SqlBasicCall) where.operands[1];
                return predicate(left, table, indexes).and(predicate(right, table, indexes));
            }
            case "or": {
                SqlBasicCall left = (SqlBasicCall) where.operands[0];
                SqlBasicCall right = (SqlBasicCall) where.operands[1];
                return predicate(left, table, indexes).or(predicate(right, table, indexes));
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
