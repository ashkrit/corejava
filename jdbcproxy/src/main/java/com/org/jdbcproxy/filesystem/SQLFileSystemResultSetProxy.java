package com.org.jdbcproxy.filesystem;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.io.File;
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
import java.util.stream.Collectors;

import static com.org.jdbcproxy.filesystem.FileSystemSQL.createTables;
import static com.org.jdbcproxy.filesystem.FileSystemSQL.loadFiles;
import static com.org.lang.MoreLang.safeExecute;
import static com.org.lang.MoreLang.safeExecuteV;

public class SQLFileSystemResultSetProxy implements InvocationHandler {

    private static final AtomicInteger sequence = new AtomicInteger();
    public static final String ROOT = "root";
    private final Map<String, BiFunction<Method, Object[], Object>> functions = new HashMap<>();

    private final int currentSequence;
    private final Connection connection;
    private final ResultSet rs;

    public SQLFileSystemResultSetProxy(SQLFileSystemStatementProxy statement, String sql) {

        this.currentSequence = sequence.incrementAndGet();
        this.connection = EmbedDatabase.open();
        createTables(connection, String.valueOf(currentSequence));
        this._addFunctions(statement);

        this._loadTable(statement, sql);
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
    }


    private void _loadTable(SQLFileSystemStatementProxy statement, String sql) {
        safeExecuteV(() -> {
                    PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(sql);
                    Table tableName = (Table) select.getFromItem();
                    System.out.printf("Querying %s \n", tableName);
                    if (isRoot(tableName)) {
                        _asRoot(statement);
                    } else {
                        _asSubFolder(statement, tableName);
                    }

                }
        );
    }

    private void _asSubFolder(SQLFileSystemStatementProxy statement, Table tableName) throws IOException {
        Path fullPath = Paths.get(_toAbsPath(statement, tableName));
        System.out.println("Loading files from " + fullPath);

        List<Path> results = Files.list(fullPath).collect(Collectors.toList());
        loadFiles(connection, String.valueOf(currentSequence), results);

    }

    private static String _toAbsPath(SQLFileSystemStatementProxy statement, Table tableName) {
        String name = tableName.toString();
        String updatedTableName = name.replace(".", File.separator);
        return updatedTableName.replace(ROOT, statement.connection.target);
    }

    private void _asRoot(SQLFileSystemStatementProxy statement) throws IOException {
        List<Path> results = Files.list(Paths.get(statement.connection.target)).collect(Collectors.toList());
        loadFiles(connection, String.valueOf(currentSequence), results);
    }

    private static boolean isRoot(Table tableName) {
        return tableName.getName().equalsIgnoreCase(ROOT);
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
