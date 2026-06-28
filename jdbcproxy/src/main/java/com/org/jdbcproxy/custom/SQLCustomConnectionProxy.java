package com.org.jdbcproxy.custom;

import com.org.jdbcproxy.SQLFactory;
import com.org.jdbcproxy.SQLFactory.SQLObjects;
import com.org.lang.MoreLang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SQLCustomConnectionProxy implements InvocationHandler {


    public static final String URL_PREFIX = "custom:";

    private final Map<String, Function<Object[], Object>> functions = new HashMap<>();

    public final String target;
    private final SQLObjects sqlObject;

    public SQLCustomConnectionProxy(String connectionUrl, SQLObjects sqlObject) {
        this.target = connectionUrl;
        this.sqlObject = sqlObject;
        functions.put("toString", param -> String.format("%s ( %s )", this.getClass().getName(), target));
        functions.put("createStatement", this::_createStatement);
    }

    private Statement _createStatement(Object[] param) {
        return SQLCustomStatementProxy.create(this,sqlObject);
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
        String cleanUrl = connectionUrl.replace(SQLCustomConnectionProxy.URL_PREFIX, "");
        SQLCustomConnectionProxy connection = new SQLCustomConnectionProxy(cleanUrl,SQLFactory.search(connectionUrl));
        return (Connection) Proxy.newProxyInstance(SQLCustomConnectionProxy.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                connection);
    }

    @Override
    public String toString() {
        return String.format("%s ( %s )", this.getClass().getName(), target);
    }
}
