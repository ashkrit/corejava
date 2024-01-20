package com.org.jdbcproxy;

import com.org.lang.MoreLang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SQLStatementProxy implements InvocationHandler {

    private final Map<String, Function<Object[], Object>> functions = new HashMap<>();

    private final Statement target;

    public SQLStatementProxy(Statement target) {
        this.target = target;
        functions.put("toString", param -> String.format("%s ( %s )", this.getClass().getName(), target.toString()));
        functions.put("executeQuery", this::_executeQuery);
    }

    private ResultSet _executeQuery(Object[] param) {
        String sql = (String) param[0];

        List<Map<Object, Object>> rows = SQLCache.result(sql);
        if (rows != null) {
            return SQLCacheResultSetProxy.create(sql,rows);
        }
        return SQLResultSetProxy.create(MoreLang.safeExecute(() -> target.executeQuery(sql)), sql, true);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Function<Object[], Object> fn = functions.getOrDefault(method.getName(), _wrap(method, args));
        return fn.apply(args);
    }

    private Function<Object[], Object> _wrap(Method method, Object[] args) {
        return $ -> MoreLang.safeExecute(() -> method.invoke(target, args));
    }

    public static Statement create(Statement statement) {
        return (Statement) Proxy.newProxyInstance(SQLStatementProxy.class.getClassLoader(), new Class<?>[]{Statement.class},
                new SQLStatementProxy(statement));
    }
}
