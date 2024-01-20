package com.org.jdbcproxy;

import com.org.lang.MoreLang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SQLResultSetProxy implements InvocationHandler {

    private final Map<String, BiFunction<Method, Object[], Object>> functions = new HashMap<>();

    private final ResultSet target;

    public SQLResultSetProxy(ResultSet target) {
        this.target = target;
        functions.put("toString", ($, param) -> String.format("%s ( %s )", this.getClass().getName(), target.toString()));
        functions.put("next", this::_next);
        functions.put("getString", this::_getString);
        functions.put("getInt", this::_getInt);
    }

    private Object _getInt(Method method, Object[] objects) {
        return MoreLang.safeExecute(() -> {
            Object colReference = objects[0];
            if (colReference instanceof String) {
                return target.getInt((String) colReference);
            } else {
                return target.getInt((Integer) colReference);
            }
        });
    }

    private Object _getString(Method method, Object[] objects) {
        return MoreLang.safeExecute(() -> {
            Object colReference = objects[0];
            if (colReference instanceof String) {
                return target.getString((String) colReference);
            } else {
                return target.getString((Integer) colReference);
            }
        });
    }

    private Object _next(Method method, Object[] objects) {
        return MoreLang.safeExecute(target::next);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        BiFunction<Method, Object[], Object> fn = functions.getOrDefault(method.getName(), _wrap(method, args));
        return fn.apply(method, args);
    }

    private BiFunction<Method, Object[], Object> _wrap(Method method, Object[] args) {
        return (v1, v2) -> MoreLang.safeExecute(() -> method.invoke(target, args));
    }

    public static ResultSet create(ResultSet rs) {
        return (ResultSet) Proxy.newProxyInstance(SQLResultSetProxy.class.getClassLoader(), new Class<?>[]{ResultSet.class},
                new SQLResultSetProxy(rs));
    }
}
