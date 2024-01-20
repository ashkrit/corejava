package com.org.jdbcproxy;

import com.org.lang.MoreLang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SQLResultSetProxy implements InvocationHandler {

    private final Map<String, Function<Object[], Object>> functions = new HashMap<>();

    private final ResultSet target;

    public SQLResultSetProxy(ResultSet target) {
        this.target = target;
        functions.put("toString", param -> String.format("%s ( %s )", this.getClass().getName(), target.toString()));
        //functions.put("executeQuery", this::_executeQuery);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Function<Object[], Object> fn = functions.getOrDefault(method.getName(), _wrap(method, args));
        return fn.apply(args);
    }

    private Function<Object[], Object> _wrap(Method method, Object[] args) {
        return $ -> MoreLang.safeExecute(() -> method.invoke(target, args));
    }

    public static ResultSet create(ResultSet rs) {
        return (ResultSet) Proxy.newProxyInstance(SQLResultSetProxy.class.getClassLoader(), new Class<?>[]{ResultSet.class},
                new SQLResultSetProxy(rs));
    }
}
