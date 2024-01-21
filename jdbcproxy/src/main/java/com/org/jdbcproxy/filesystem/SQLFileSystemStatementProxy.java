package com.org.jdbcproxy.filesystem;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.org.lang.MoreLang.safeExecuteV;

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

        return SQLFileSystemResultSetProxy.create(this, sql);
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
