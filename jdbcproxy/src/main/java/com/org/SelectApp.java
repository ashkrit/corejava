package com.org;


import com.org.jdbcproxy.fs.SQLFileSystemConnectionProxy;
import com.org.jdbcproxy.rdbms.SQLCache;
import com.org.jdbcproxy.SQLDriverProxy;

import java.sql.*;
import java.util.List;
import java.util.Map;


/**
 * jdbc/proxy/key=jdbc:sqlite:/tmp/offers.db
 * jdbc/proxy/key=filesystem:
 */

public class SelectApp {

    static Class<?> driver = org.sqlite.JDBC.class;

    public static void main(String[] args) throws Exception {

        String dbPath = args[0];


        Class.forName(driver.getName());


        SQLDriverProxy.register();

        //_rdbms(dbPath);


        Connection fsConnection = DriverManager.getConnection(SQLDriverProxy.JDBC_PROXY_KEY + SQLFileSystemConnectionProxy.URL_PREFIX);
        Statement fsstatement = fsConnection.createStatement();
        dumpResult(fsstatement, "select * from fs('/Users/ashkrit/_tmp/data') order by size");

    }

    private static void dumpResult(Statement fsstatement, String sql) throws SQLException {
        System.out.println("Executing " + sql);
        ResultSet r = fsstatement.executeQuery(sql);
        while (r.next()) {


            String name = r.getString("name");
            String fullPath = r.getString("full_path");
            long size = r.getLong("size");
            Timestamp d = r.getTimestamp("last_modified");
            boolean isFile = r.getBoolean("is_file");
            boolean isFolder = r.getBoolean("is_folder");
            Object isHidden = r.getObject("is_hidden");
            byte[] fileContent = r.getBytes("content");
            System.out.printf("fullPath %s Name %s , size %s , last modified %s , is_file %s , is_folder %s , is_hidden %s , File Peek %s %n",
                    fullPath, name, size, d, isFile, isFolder, isHidden, fileContent.length);
        }
    }

    private static void _rdbms(String dbPath) throws SQLException {
        String sqlLiteConnectionString = "jdbc:sqlite:" + dbPath;
        Connection connection = DriverManager.getConnection(sqlLiteConnectionString);
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);  // set timeout to 30 sec.

        statement.executeUpdate("drop table if exists person");
        statement.executeUpdate("create table person (id integer, name string)");
        statement.executeUpdate("insert into person values(1, 'leo')");
        statement.executeUpdate("insert into person values(2, 'yui')");

        execute(connection, "select * from person");

        connection.close();

        Connection proxyConnection = DriverManager.getConnection(SQLDriverProxy.JDBC_PROXY_KEY + sqlLiteConnectionString);

        execute(proxyConnection, "select * from person");
        execute(proxyConnection, "select * from person");
    }

    private static void execute(Connection connection, String sql) throws SQLException {
        System.out.println("Connection  Class:" + connection);
        Statement statement = connection.createStatement();
        System.out.println("Statement Class " + statement);
        ResultSet rs = statement.executeQuery(sql);
        System.out.println("ResultSet Class " + rs);
        while (rs.next()) {
            // read the result set
            System.out.println("name = " + rs.getString("name"));
            System.out.println("id = " + rs.getString(1));
            System.out.println("id = " + rs.getInt("id"));
        }

        List<Map<Object, Object>> result = SQLCache.result(sql);

        System.out.println(result);
    }


}
