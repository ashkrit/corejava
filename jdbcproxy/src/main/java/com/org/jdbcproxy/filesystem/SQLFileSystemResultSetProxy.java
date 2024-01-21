package com.org.jdbcproxy.filesystem;

import com.org.jdbcproxy.SQLCache;
import com.org.lang.MoreLang;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SQLFileSystemResultSetProxy implements InvocationHandler {

    private final Map<String, BiFunction<Method, Object[], Object>> functions = new HashMap<>();

    private final SQLFileSystemStatementProxy statement;
    private List<Path> results;
    private Map<Object, Object> currentRow = new HashMap<>();
    private int index = -1;

    private final Map<String, BiConsumer<Path, Map<Object, Object>>> columnSelections = new HashMap<>();

    public SQLFileSystemResultSetProxy(SQLFileSystemStatementProxy statement, String sql) {
        this.statement = statement;
        this.functions.put("toString", ($, param) -> String.format("%s ( %s )", this.getClass().getName(), statement.toString()));
        this.functions.put("next", this::_next);
        this.functions.put("getString", this::_getString);

        System.out.println(statement.connection.target);

        columnSelections.put("*", allFields);

        MoreLang.safeExecuteV(() -> {
                    PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sql);

                    Table tableName = (Table) select.getFromItem();
                    System.out.printf("Querying %s \n", tableName);

                    Expression where = select.getWhere();
                    System.out.printf("Where %s \n", where);

                    List<SelectItem<?>> columns = select.getSelectItems();
                    System.out.printf("Columns %s \n", columns);

                    Set<String> tableNames = TablesNamesFinder.findTables(sql);
                    System.out.printf("Table names %s \n", tableNames);

                    if (tableName.getName().equalsIgnoreCase("root")) {
                        results = Files.list(Paths.get(statement.connection.target)).collect(Collectors.toList());
                    }

                }
        );

    }

    private Object _getString(Method method, Object[] objects) {
        return currentRow.get(objects[0]);
    }

    private Object _next(Method $, Object[] param) {
        index++;
        boolean result = index < results.size();
        if (result) {
            currentRow.clear();
            columnSelections.get("*").accept(results.get(index), currentRow);
        }
        return result;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        BiFunction<Method, Object[], Object> fn = functions.getOrDefault(method.getName(), _wrap(method, args));
        return fn.apply(method, args);
    }

    private BiFunction<Method, Object[], Object> _wrap(Method method, Object[] args) {
        return (v1, v2) -> {
            throw new IllegalArgumentException("Method " + method.getName() + " is not supported");
        };
    }

    public static ResultSet create(SQLFileSystemStatementProxy statement, String sql) {
        return (ResultSet) Proxy.newProxyInstance(SQLFileSystemResultSetProxy.class.getClassLoader(), new Class<?>[]{ResultSet.class},
                new SQLFileSystemResultSetProxy(statement, sql));
    }

    @Override
    public String toString() {
        return String.format("%s ( %s )", this.getClass().getName(), statement);
    }

    static BiConsumer<Path, Map<Object, Object>> allFields = (p, container) -> {
        File file = p.toFile();
        container.put("name", file.getName());
        container.put("last_modified", file.lastModified());
        container.put("size", file.length());
    };
}
