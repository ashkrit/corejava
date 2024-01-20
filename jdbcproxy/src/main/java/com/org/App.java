package com.org;


import com.org.jdbcproxy.SQLCache;
import com.org.jdbcproxy.SQLDriverProxy;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class App {

    static Class<?> driver = org.sqlite.JDBC.class;

    public static void main(String[] args) throws Exception {

        String dbPath = args[0];


        Class.forName(driver.getName());


        SQLDriverProxy.register();

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

        Connection proxyConnection = DriverManager.getConnection("jdbc/proxy/key=" + sqlLiteConnectionString);

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
            System.out.println("name = " + rs.getString(1));
            System.out.println("id = " + rs.getInt("id"));
        }

        List<Map<Object, Object>> result = SQLCache.result(sql);

        System.out.println(result);
    }


}
