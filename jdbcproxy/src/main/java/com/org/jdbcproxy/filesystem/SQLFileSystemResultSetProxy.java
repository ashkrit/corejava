package com.org.jdbcproxy.filesystem;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;

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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.org.jdbcproxy.filesystem.FileSystemFields.*;
import static com.org.lang.MoreLang.safeExecuteV;

public class SQLFileSystemResultSetProxy implements InvocationHandler {

    private final Map<String, BiFunction<Method, Object[], Object>> functions = new HashMap<>();

    private final SQLFileSystemStatementProxy statement;
    private List<Path> results;
    private final Map<Object, Object> currentRow = new HashMap<>();
    private int index = -1;

    private final Map<String, BiConsumer<Path, Map<Object, Object>>> columnSelections = new HashMap<>();

    public SQLFileSystemResultSetProxy(SQLFileSystemStatementProxy statement, String sql) {
        this.statement = statement;
        this._configureColumnsExtractor();
        this._parseQuery(statement, sql);
        this._addFunctions(statement);


    }

    private void _addFunctions(SQLFileSystemStatementProxy statement) {
        this.functions.put("toString", ($, param) -> String.format("%s ( %s )", this.getClass().getName(), statement.toString()));
        this.functions.put("next", this::_next);
        this.functions.put("getString", this::_getString);
        this.functions.put("getInt", this::_getInt);
        this.functions.put("getLong", this::_getLong);
        this.functions.put("getDate", this::_getDate);
    }


    private void _configureColumnsExtractor() {
        columnSelections.put("*", allFields);
    }

    private void _parseQuery(SQLFileSystemStatementProxy statement, String sql) {
        safeExecuteV(() -> {
                    PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sql);

                    Table tableName = (Table) select.getFromItem();
                    System.out.printf("Querying %s \n", tableName);

                    Expression where = select.getWhere();
                    System.out.printf("Where %s \n", where);

                    if (isRoot(tableName)) {
                        results = Files.list(Paths.get(statement.connection.target)).collect(Collectors.toList());
                    }

                }
        );
    }

    private static boolean isRoot(Table tableName) {
        return tableName.getName().equalsIgnoreCase("root");
    }

    private Object _getDate(Method method, Object[] objects) {
        Object value = _readValue(objects);
        if (value instanceof Long) {
            return new java.sql.Date((Long) value);
        }
        return value;
    }

    private Object _getLong(Method method, Object[] objects) {
        Object value = _readValue(objects);
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        return value;
    }

    private Object _getInt(Method method, Object[] objects) {
        Object value = _readValue(objects);
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }

        return value;
    }

    private Object _getString(Method method, Object[] objects) {
        Object value = _readValue(objects);
        return value != null ? value.toString() : null;
    }

    private Object _readValue(Object[] objects) {
        String fieldName = ((String) objects[0]).toLowerCase();
        if (!currentRow.containsKey(fieldName)) {
            throw new IllegalArgumentException("Field " + fieldName + " is not present in the result set ( " + currentRow.keySet() + " )");
        }
        Object value = currentRow.get(fieldName);
        return value;
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
        container.put(FILE_NAME, file.getName());
        container.put(LAST_MODIFIED_TS, file.lastModified());
        container.put(FILE_SIZE, file.length());
    };
}
