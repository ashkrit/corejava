package com.org.jdbcproxy.custom;

import com.org.lang.MoreLang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SQLCustomStatementProxy implements InvocationHandler {

    private final Map<String, Function<Object[], Object>> functions = new HashMap<>();

    public final SQLCustomConnectionProxy connection;

    public SQLCustomStatementProxy(SQLCustomConnectionProxy connection) {
        this.connection = connection;
        functions.put("toString", param -> String.format("%s ( %s )", this.getClass().getName(), connection));
        //functions.put("executeQuery", this::_executeQuery);
        functions.put("executeUpdate", this::_executeUpdate);
    }

    private Object _executeUpdate(Object[] objects) {
        String sql = (String) objects[0];
        MoreLang.safeExecuteV(() -> {
            RowInfo row = InsertStatement.createRowInfo(sql);
            System.out.printf("Insert In table %s , Values %s%n", row.tableName, row.values);
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

    public static Statement create(SQLCustomConnectionProxy connection) {
        SQLCustomStatementProxy statement = new SQLCustomStatementProxy(connection);
        return (Statement) Proxy.newProxyInstance(
                SQLCustomStatementProxy.class.getClassLoader(), new Class<?>[]{Statement.class}, statement);
    }

    @Override
    public String toString() {
        return String.format("%s ( %s )", this.getClass().getName(), connection);
    }
}
