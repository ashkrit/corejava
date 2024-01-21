package com.org.jdbcproxy.rdbms;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class SQLCacheResultSetProxy implements InvocationHandler {

    private final Map<String, BiFunction<Method, Object[], Object>> functions = new HashMap<>();

    private final List<Map<Object, Object>> results;
    private int index = -1;

    public SQLCacheResultSetProxy(String sql, List<Map<Object, Object>> results) {
        this.results = results;
        functions.put("toString", ($, param) -> String.format("%s ", this.getClass().getName()));
        functions.put("next", this::_next);
        functions.put("getString", this::_getString);
        functions.put("getInt", this::_getInt);
    }

    private Object _getInt(Method method, Object[] objects) {
        Object colReference = objects[0];
        return results.get(index).get(colReference);
    }


    private Object _getString(Method method, Object[] objects) {
        Object colReference = objects[0];
        return results.get(index).get(colReference);
    }

    private Object _next(Method method, Object[] objects) {
        index++;
        return index < results.size();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        BiFunction<Method, Object[], Object> fn = functions.getOrDefault(method.getName(), _wrap(method, args));
        return fn.apply(method, args);
    }

    private BiFunction<Method, Object[], Object> _wrap(Method method, Object[] args) {
        return (v1, v2) -> {
            throw new IllegalArgumentException("Not supported " + method.getName());
        };
    }

    public static ResultSet create(String sql, List<Map<Object, Object>> rows) {
        return (ResultSet) Proxy.newProxyInstance(SQLCacheResultSetProxy.class.getClassLoader(), new Class<?>[]{ResultSet.class},
                new SQLCacheResultSetProxy(sql, rows));
    }
}
