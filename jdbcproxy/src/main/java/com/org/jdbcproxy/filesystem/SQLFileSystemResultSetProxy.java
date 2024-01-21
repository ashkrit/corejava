package com.org.jdbcproxy.filesystem;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.TableFunction;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.org.jdbcproxy.filesystem.FileSystemSQL.createTables;
import static com.org.jdbcproxy.filesystem.FileSystemSQL.loadFiles;
import static com.org.lang.MoreLang.safeExecute;
import static com.org.lang.MoreLang.safeExecuteV;

public class SQLFileSystemResultSetProxy implements InvocationHandler {

    private static final AtomicInteger sequence = new AtomicInteger();
    private final Map<String, BiFunction<Method, Object[], Object>> functions = new HashMap<>();
    private final Map<String, BiFunction<Method, Object[], Object>> columnValues = new HashMap<>();

    private final int currentSequence;
    private final Connection connection;
    private final ResultSet rs;

    public SQLFileSystemResultSetProxy(SQLFileSystemStatementProxy statement, String sql) {

        this.currentSequence = sequence.incrementAndGet();
        this.connection = EmbedDatabase.open();
        createTables(connection, String.valueOf(currentSequence));
        this._addFunctions(statement);
        this._addFieldFunctions();

        this._loadTable(sql);
        this.rs = _executeQuery(sql);
    }

    private ResultSet _executeQuery(String sql) {
        return safeExecute(() -> {
            PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sql);
            String tableName = select.getFromItem().toString();
            String updatedSql = sql.replace(tableName, String.format("files_%s", currentSequence));
            return this.connection.createStatement().executeQuery(updatedSql);
        });
    }

    private void _addFunctions(SQLFileSystemStatementProxy statement) {
        this.functions.put("toString", ($, param) -> String.format("%s ( %s )", this.getClass().getName(), statement.toString()));
        this.functions.put("getString", this::_getString);
        this.functions.put("getBytes", this::_getBytes);
    }


    private void _addFieldFunctions() {

        this.columnValues.put("content", this::_getFileContent);
        this.columnValues.put("content_bytes", this::_getFileContentAsBytes);
    }

    private Object _getBytes(Method method, Object[] args) {
        String columnName = (String) args[0];
        BiFunction<Method, Object[], Object> fn = columnValues.get(columnName.toLowerCase() + "_bytes");
        if (fn != null) {
            return fn.apply(method, args);
        }
        return safeExecute(() -> method.invoke(rs, args));
    }

    private Object _getFileContentAsBytes(Method $, Object[] $$) {
        return safeExecute(() -> {
            String fullPath = rs.getString(FileSystemFields.FULL_PATH);
            return Files.readAllBytes(Paths.get(fullPath));
        });
    }

    private Object _getFileContent(Method $, Object[] $$) {
        return safeExecute(() -> {
            String fullPath = rs.getString(FileSystemFields.FULL_PATH);
            return new String(Files.readAllBytes(Paths.get(fullPath)));
        });
    }

    private Object _getString(Method method, Object[] args) {
        String columnName = (String) args[0];
        BiFunction<Method, Object[], Object> fn = columnValues.get(columnName.toLowerCase());
        if (fn != null) {
            return fn.apply(method, args);
        }
        return safeExecute(() -> method.invoke(rs, args));
    }


    private void _loadTable(String sql) {
        safeExecuteV(() -> {
                    PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sql);
                    TableFunction tableName = (TableFunction) select.getFromItem();
                    String name = tableName.toString();
                    Pattern namePattern = Pattern.compile("fs\\('([^']+)'\\)");
                    Matcher matcher = namePattern.matcher(name);
                    if (matcher.matches()) {
                        _asFolder(matcher.group(1));
                    } else {
                        throw new IllegalArgumentException(String.format("Unable to find folder from %s , eg fs('/tmp/data')", name));
                    }

                }
        );
    }

    private void _asFolder(String path) throws IOException {
        Path fullPath = Paths.get(path);
        System.out.println("Loading files from " + fullPath);

        List<Path> results = Files.list(fullPath).collect(Collectors.toList());
        loadFiles(connection, String.valueOf(currentSequence), results);

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        BiFunction<Method, Object[], Object> fn = functions.getOrDefault(method.getName(), _wrap(method, args));
        return fn.apply(method, args);
    }

    private BiFunction<Method, Object[], Object> _wrap(Method method, Object[] args) {
        return (v1, v2) -> safeExecute(() -> method.invoke(rs, args));
    }

    public static ResultSet create(SQLFileSystemStatementProxy statement, String sql) {
        SQLFileSystemResultSetProxy realClass = new SQLFileSystemResultSetProxy(statement, sql);

        return (ResultSet) Proxy.newProxyInstance(SQLFileSystemResultSetProxy.class.getClassLoader(),
                new Class<?>[]{ResultSet.class},
                realClass);
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }
}
