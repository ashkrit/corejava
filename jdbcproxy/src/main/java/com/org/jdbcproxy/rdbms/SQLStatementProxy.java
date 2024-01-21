package com.org.jdbcproxy.rdbms;

import com.org.jdbcproxy.SQLCache;
import com.org.lang.MoreLang;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.org.lang.MoreLang.safeExecuteV;

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

        _parse(sql);

        List<Map<Object, Object>> rows = SQLCache.result(sql);
        if (rows != null) {
            return SQLCacheResultSetProxy.create(sql, rows);
        }
        return SQLResultSetProxy.create(MoreLang.safeExecute(() -> target.executeQuery(sql)), sql, true);
    }

    private static void _parse(String sql) {
        safeExecuteV(() -> {

            PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sql);

            Table tableName = (Table) select.getFromItem();
            System.out.printf("Querying %s \n", tableName);

            Expression where = select.getWhere();
            System.out.printf("Where %s \n", where);

            Set<String> tableNames = TablesNamesFinder.findTables(sql);
            System.out.printf("Table names %s \n", tableNames);
        });
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
