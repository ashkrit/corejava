package com.org.jdbcproxy.filesystem;

import com.org.lang.MoreLang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SQLFileSystemConnectionProxy implements InvocationHandler {


    public static final String URL_PREFIX = "filesystem:";

    private final Map<String, Function<Object[], Object>> functions = new HashMap<>();

    public final String target;

    public SQLFileSystemConnectionProxy(String connectionUrl) {
        this.target = connectionUrl;
        functions.put("toString", param -> String.format("%s ( %s )", this.getClass().getName(), target));
        functions.put("createStatement", this::_createStatement);
    }

    private Statement _createStatement(Object[] param) {
        return SQLFileSystemStatementProxy.create(this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Function<Object[], Object> fn = functions.getOrDefault(method.getName(), _wrap(method, args));
        return fn.apply(args);
    }

    private Function<Object[], Object> _wrap(Method method, Object[] args) {
        return $ -> MoreLang.safeExecute(() -> method.invoke(target, args));
    }

    public static Connection create(String connectionUrl) {
        String cleanUrl = connectionUrl.replace(SQLFileSystemConnectionProxy.URL_PREFIX, "");
        return (Connection) Proxy.newProxyInstance(SQLFileSystemConnectionProxy.class.getClassLoader(), new Class<?>[]{Connection.class},
                new SQLFileSystemConnectionProxy(cleanUrl));
    }

    @Override
    public String toString() {
        return String.format("%s ( %s )", this.getClass().getName(), target);
    }
}
