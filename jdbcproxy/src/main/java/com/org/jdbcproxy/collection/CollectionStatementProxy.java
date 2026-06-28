package com.org.jdbcproxy.collection;

import com.org.jdbcproxy.SQLFactory;
import com.org.jdbcproxy.custom.CustomDataSourceContext;
import com.org.jdbcproxy.custom.InsertStatement;
import com.org.jdbcproxy.custom.RowInfo;
import com.org.jdbcproxy.custom.SQLCustomConnectionProxy;
import com.org.lang.MoreLang;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.TableFunction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CollectionStatementProxy implements InvocationHandler {

    private final Map<String, Function<Object[], Object>> functions = new HashMap<>();

    public final CollectionConnectionProxy connection;
    private final SQLFactory.SQLObjects sqlObject;

    public CollectionStatementProxy(CollectionConnectionProxy connection, SQLFactory.SQLObjects sqlObject) {
        this.connection = connection;
        this.sqlObject = sqlObject;
        _registerFunctions(connection);
    }

    private void _registerFunctions(CollectionConnectionProxy connection) {
        functions.put("toString", param -> String.format("%s ( %s )", this.getClass().getName(), connection));
        functions.put("executeQuery", this::_executeQuery);
    }

    private Object _executeQuery(Object[] objects) {
        String sql = (String) objects[0];
        return MoreLang.safeExecute(() -> {
            CustomDataSourceContext context = this.sqlObject.context.get();
            Connection innerConnection = context.connection.get();
            String tableName = _tableName(sql);

            CustomDataSourceContext.CollectionTable<?> tableDef = context.collectionLoadFunctions.get(tableName.toLowerCase());


            return innerConnection.createStatement().executeQuery(sql);
        });
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Function<Object[], Object> fn = functions.getOrDefault(method.getName(), _wrap(method, args));
        return fn.apply(args);
    }

    private Function<Object[], Object> _wrap(Method method, Object[] $) {
        return $$ -> {
            throw new IllegalArgumentException("Method " + method.getName() + " is not supported");
        };
    }

    public static Statement create(CollectionConnectionProxy connection, SQLFactory.SQLObjects sqlObject) {
        CollectionStatementProxy statement = new CollectionStatementProxy(connection, sqlObject);
        return (Statement) Proxy.newProxyInstance(
                CollectionStatementProxy.class.getClassLoader(), new Class<?>[]{Statement.class}, statement);
    }

    private static String _tableName(String sql) throws JSQLParserException {
        PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sql);
        TableFunction tableName = (TableFunction) select.getFromItem();
        return tableName.toString();
    }

    @Override
    public String toString() {
        return String.format("%s ( %s )", this.getClass().getName(), connection);
    }
}
