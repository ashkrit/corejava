package com.org.jdbcproxy.rdbms;

import com.org.lang.MoreLang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SQLConnectionProxy implements InvocationHandler {

    private final Map<String, Function<Object[], Object>> functions = new HashMap<>();

    private final Connection target;

    public SQLConnectionProxy(Connection target) {
        this.target = target;
        functions.put("toString", param -> String.format("%s ( %s )", this.getClass().getName(), target.toString()));
        functions.put("createStatement", this::_createStatement);
    }

    private Statement _createStatement(Object[] param) {
        return SQLStatementProxy.create(MoreLang.safeExecute(target::createStatement));
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Function<Object[], Object> fn = functions.getOrDefault(method.getName(), _wrap(method, args));
        return fn.apply(args);
    }

    private Function<Object[], Object> _wrap(Method method, Object[] args) {
        return $ -> MoreLang.safeExecute(() -> method.invoke(target, args));
    }

    public static Connection create(Connection connection) {
        return (Connection) Proxy.newProxyInstance(SQLConnectionProxy.class.getClassLoader(), new Class<?>[]{Connection.class},
                new SQLConnectionProxy(connection));
    }
}
