package com.org.jdbcproxy.filesystem;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SQLFileSystemStatementProxy implements InvocationHandler {

    private final Map<String, Function<Object[], Object>> functions = new HashMap<>();

    public final SQLFileSystemConnectionProxy connection;

    public SQLFileSystemStatementProxy(SQLFileSystemConnectionProxy connection) {
        this.connection = connection;
        functions.put("toString", param -> String.format("%s ( %s )", this.getClass().getName(), connection));
        functions.put("executeQuery", this::_executeQuery);
    }

    private ResultSet _executeQuery(Object[] param) {
        String sql = (String) param[0];
        if (SQLFileSystemResultSetProxy.canProcess(sql)) {
            return SQLFileSystemResultSetProxy.create(this, sql);
        }
        throw new IllegalArgumentException(String.format("Unable to execute %s", sql));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Function<Object[], Object> fn = functions.getOrDefault(method.getName(), _wrap(method, args));
        return fn.apply(args);
    }

    private Function<Object[], Object> _wrap(Method method, Object[] args) {
        return $ -> {
            throw new IllegalArgumentException("Method " + method.getName() + " is not supported");
        };
    }

    public static Statement create(SQLFileSystemConnectionProxy connection) {
        return (Statement) Proxy.newProxyInstance(SQLFileSystemStatementProxy.class.getClassLoader(), new Class<?>[]{Statement.class},
                new SQLFileSystemStatementProxy(connection));
    }

    @Override
    public String toString() {
        return String.format("%s ( %s )", this.getClass().getName(), connection);
    }
}
