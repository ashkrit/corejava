package com.org.jdbcproxy.rdbms;

import com.org.jdbcproxy.SQLCache;
import com.org.lang.MoreLang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class SQLResultSetProxy implements InvocationHandler {

    private final Map<String, BiFunction<Method, Object[], Object>> functions = new HashMap<>();

    private final ResultSet target;
    private final boolean cache;
    private List<Map<Object, Object>> results;
    private int index = -1;

    public SQLResultSetProxy(ResultSet target, String sql, boolean cache) {
        this.target = target;
        functions.put("toString", ($, param) -> String.format("%s ( %s )", this.getClass().getName(), target.toString()));
        functions.put("next", this::_next);
        functions.put("getString", this::_getString);
        functions.put("getInt", this::_getInt);
        this.cache = cache;
        if (cache) {
            this.results = SQLCache.newQuery(sql);
        }
    }

    private Object _getInt(Method method, Object[] objects) {
        Object colReference = objects[0];
        Object value = MoreLang.safeExecute(() -> {

            if (colReference instanceof String) {
                return target.getInt((String) colReference);
            } else {
                return target.getInt((Integer) colReference);
            }
        });

        _cache(colReference, value);
        return value;
    }

    private void _cache(Object colReference, Object value) {
        if (cache) {
            results.get(index).put(colReference, value);
        }
    }

    private Object _getString(Method method, Object[] objects) {
        Object colReference = objects[0];

        Object value = MoreLang.safeExecute(() -> {
            if (colReference instanceof String) {
                return target.getString((String) colReference);
            } else {
                return target.getString((Integer) colReference);
            }
        });
        _cache(colReference, value);
        return value;
    }

    private Object _next(Method method, Object[] objects) {
        boolean hasValue = MoreLang.safeExecute(target::next);
        if (cache && hasValue) {
            results.add(new HashMap<>());
            index++;
        }
        return hasValue;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        BiFunction<Method, Object[], Object> fn = functions.getOrDefault(method.getName(), _wrap(method, args));
        return fn.apply(method, args);
    }

    private BiFunction<Method, Object[], Object> _wrap(Method method, Object[] args) {
        return (v1, v2) -> MoreLang.safeExecute(() -> method.invoke(target, args));
    }

    public static ResultSet create(ResultSet rs, String sql, boolean cache) {
        return (ResultSet) Proxy.newProxyInstance(SQLResultSetProxy.class.getClassLoader(), new Class<?>[]{ResultSet.class},
                new SQLResultSetProxy(rs, sql, cache));
    }
}
