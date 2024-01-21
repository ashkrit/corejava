package com.org;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InsertApp {

    public static void main(String[] args) throws JSQLParserException {
        RowInfo value = createRowInfo("insert into merchant(key,value, total) values('k1','v1',99)");
        System.out.printf("Table %s , Values %s%n", value.tableName, value.values);
    }

    private static RowInfo createRowInfo(String sql) throws JSQLParserException {
        Insert stmt = (Insert) CCJSqlParserUtil.parse(sql);
        ExpressionList<?> v = stmt.getValues().getExpressions();
        ExpressionList<Column> cols = stmt.getColumns();
        Map<String, Object> values = IntStream.range(0, v.size())
                .boxed()
                .collect(Collectors.toMap(index -> getColumnName(index, cols), index -> columnValue(v.get(index))));

        return new RowInfo(stmt.getTable().getName(), values);
    }

    private static String getColumnName(Integer index, ExpressionList<Column> cols) {
        return cols.get(index).getColumnName();
    }

    private static Object columnValue(Expression o) {
        if (o instanceof StringValue) {
            return ((StringValue) o).getValue();
        } else if (o instanceof LongValue) {
            return ((LongValue) o).getValue();
        }
        return o.toString();
    }

    public static class RowInfo {

        public final Map<String, Object> values;
        public final String tableName;

        public RowInfo(String tableName, Map<String, Object> values) {
            this.values = values;
            this.tableName = tableName;
        }
    }
}
