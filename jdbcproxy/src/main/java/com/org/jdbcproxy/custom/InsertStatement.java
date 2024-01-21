package com.org.jdbcproxy.custom;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InsertStatement {

    public static RowInfo createRowInfo(String sql) throws JSQLParserException {
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

    private static Object columnValue(Expression expression) {
        if (expression instanceof StringValue) {
            return ((StringValue) expression).getValue();
        } else if (expression instanceof LongValue) {
            return ((LongValue) expression).getValue();
        } else if (expression instanceof Parenthesis) {
            return columnValue(((Parenthesis) expression).getExpression());
        }
        return expression.toString();
    }
}
