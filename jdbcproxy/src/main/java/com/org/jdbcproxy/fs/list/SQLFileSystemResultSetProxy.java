package com.org.jdbcproxy.fs.list;

import com.org.jdbcproxy.EmbedDatabase;
import com.org.jdbcproxy.fs.SQLFileSystemStatementProxy;
import net.sf.jsqlparser.JSQLParserException;
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

import static com.org.jdbcproxy.fs.list.FileSystemSQL.createTables;
import static com.org.jdbcproxy.fs.list.FileSystemSQL.loadFiles;
import static com.org.lang.MoreLang.safeExecute;
import static com.org.lang.MoreLang.safeExecuteV;

public class SQLFileSystemResultSetProxy implements InvocationHandler {

    private static final AtomicInteger sequence = new AtomicInteger();
    public static final String FILESYSTEM_PATTERN = "fs\\('([^']+)'\\)";
    private final Map<String, BiFunction<Method, Object[], Object>> functions = new HashMap<>();
    private final Map<String, BiFunction<Method, Object[], Object>> virtualColumns = new HashMap<>();

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
        this.functions.put("toString", ($, $$) -> String.format("%s ( %s )", this.getClass().getName(), statement.toString()));
        this.functions.put("getString", this::_getString);
        this.functions.put("getBytes", this::_getBytes);
    }


    private void _addFieldFunctions() {

        this.virtualColumns.put(FileSystemFields.CONTENT, this::_getFileContent);
        this.virtualColumns.put(FileSystemFields.CONTENT + FileSystemFields.BYTES, this::_getFileContentAsBytes);
    }

    private Object _getBytes(Method method, Object[] args) {
        String columnName = (String) args[0];
        BiFunction<Method, Object[], Object> fn = virtualColumns.get(columnName.toLowerCase() + FileSystemFields.BYTES);
        if (fn != null) {
            return fn.apply(method, args);
        }
        return safeExecute(() -> method.invoke(rs, args));
    }

    private Object _getFileContentAsBytes(Method $, Object[] $$) {
        return safeExecute(() -> {
            String fullPath = rs.getString(FileSystemFields.FULL_PATH);
            if (rs.getBoolean(FileSystemFields.IS_FILE)) {
                return Files.readAllBytes(Paths.get(fullPath));
            }
            return new byte[0];
        });
    }

    private Object _getFileContent(Method $, Object[] $$) {
        return safeExecute(() -> {
            String fullPath = rs.getString(FileSystemFields.FULL_PATH);
            if (rs.getBoolean(FileSystemFields.IS_FILE)) {
                return new String(Files.readAllBytes(Paths.get(fullPath)));
            }
            return "";
        });
    }

    private Object _getString(Method method, Object[] args) {
        String columnName = (String) args[0];
        BiFunction<Method, Object[], Object> fn = virtualColumns.get(columnName.toLowerCase());
        if (fn != null) {
            return fn.apply(method, args);
        }
        return safeExecute(() -> method.invoke(rs, args));
    }


    private void _loadTable(String sql) {
        safeExecuteV(() -> {
                    String name = _tableName(sql);
                    Matcher matcher = _match(name);
                    if (matcher.matches()) {
                        _asFolder(matcher.group(1));
                    } else {
                        throw new IllegalArgumentException(String.format("Unable to find folder from %s , eg fs('/tmp/data')", name));
                    }

                }
        );
    }

    private static String _tableName(String sql) throws JSQLParserException {
        PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sql);
        TableFunction tableName = (TableFunction) select.getFromItem();
        return tableName.toString();
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


    public static boolean canProcess(String sql) {
        return safeExecute(() -> _match(_tableName(sql)).matches());
    }

    private static Matcher _match(String name) {
        Pattern namePattern = Pattern.compile(FILESYSTEM_PATTERN);
        return namePattern.matcher(name);
    }
}
