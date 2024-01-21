package com.org;


import com.org.jdbcproxy.SQLCache;
import com.org.jdbcproxy.SQLDriverProxy;
import com.org.jdbcproxy.SQLFactory;
import com.org.jdbcproxy.SQLFactory.SQLObjects;
import com.org.jdbcproxy.filesystem.SQLFileSystemConnectionProxy;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class App {

    static Class<?> driver = org.sqlite.JDBC.class;

    public static void main(String[] args) throws Exception {

        String dbPath = args[0];


        Class.forName(driver.getName());


        SQLDriverProxy.register();
        SQLFactory.register("filesystem", new SQLObjects(SQLFileSystemConnectionProxy::create) {
            @Override
            public boolean accept(String value) {
                return value.startsWith(SQLFileSystemConnectionProxy.URL_PREFIX);
            }

        });

        //_rdbms(dbPath);


        Connection fsConnection = DriverManager.getConnection(SQLDriverProxy.JDBC_PROXY_KEY + "filesystem:/Users/ashkrit/_tmp/db");
        System.out.println("Connection :" + fsConnection);
        Statement fsstatement = fsConnection.createStatement();
        System.out.println("Statement :" + fsstatement);
        ResultSet r = fsstatement.executeQuery("select * from root");
        System.out.println("result Set:" + r);
        while (r.next()) {


            String name = r.getString("name");
            long lastModified = r.getLong("last_modified");
            long size = r.getLong("size");

            System.out.println(name + " " + lastModified + " " + size);
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
