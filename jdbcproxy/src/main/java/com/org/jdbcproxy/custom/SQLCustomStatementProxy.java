package com.org.jdbcproxy.custom;

import com.org.jdbcproxy.SQLFactory;
import com.org.lang.MoreLang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SQLCustomStatementProxy implements InvocationHandler {

    private final Map<String, Function<Object[], Object>> functions = new HashMap<>();

    public final SQLCustomConnectionProxy connection;
    private final SQLFactory.SQLObjects sqlObject;

    public SQLCustomStatementProxy(SQLCustomConnectionProxy connection, SQLFactory.SQLObjects sqlObject) {
        this.connection = connection;
        this.sqlObject = sqlObject;
        _registerFunctions(connection);
    }

    private void _registerFunctions(SQLCustomConnectionProxy connection) {
        functions.put("toString", param -> String.format("%s ( %s )", this.getClass().getName(), connection));
        functions.put("executeQuery", this::_executeQuery);
        functions.put("executeUpdate", this::_executeUpdate);
    }

    private Object _executeQuery(Object[] objects) {
        String sql = (String) objects[0];
        return MoreLang.safeExecute(() -> {
            CustomDataSourceContext context = this.sqlObject.context.get();
            Connection innerConnection = context.connection.get();
            return innerConnection.createStatement().executeQuery(sql);
        });
    }

    private Object _executeUpdate(Object[] objects) {
        String sql = (String) objects[0];
        MoreLang.safeExecuteV(() -> {
            RowInfo row = InsertStatement.createRowInfo(sql);
            CustomDataSourceContext context = this.sqlObject.context.get();
            Connection innerConnection = context.connection.get();
            System.out.printf("Insert In table %s , Values %s%n", row.tableName, row.values);
            context.loadFunctions.get(row.tableName.toLowerCase()).accept(innerConnection, row);
        });
        return 1;
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

    public static Statement create(SQLCustomConnectionProxy connection, SQLFactory.SQLObjects sqlObject) {
        SQLCustomStatementProxy statement = new SQLCustomStatementProxy(connection, sqlObject);
        return (Statement) Proxy.newProxyInstance(
                SQLCustomStatementProxy.class.getClassLoader(), new Class<?>[]{Statement.class}, statement);
    }

    @Override
    public String toString() {
        return String.format("%s ( %s )", this.getClass().getName(), connection);
    }
}
