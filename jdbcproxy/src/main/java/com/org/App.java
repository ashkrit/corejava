package com.org;


import com.org.jdbcproxy.SQLDriver;

import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class App {

    static Class<?> driver = org.sqlite.JDBC.class;

    public static void main(String[] args) throws Exception {

        String dbPath = args[0];


        Class.forName(driver.getName());


        SQLDriver.register();

        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);  // set timeout to 30 sec.

        statement.executeUpdate("drop table if exists person");
        statement.executeUpdate("create table person (id integer, name string)");
        statement.executeUpdate("insert into person values(1, 'leo')");
        statement.executeUpdate("insert into person values(2, 'yui')");

        ResultSet rs = statement.executeQuery("select * from person");
        while (rs.next()) {
            // read the result set
            System.out.println("name = " + rs.getString("name"));
            System.out.println("id = " + rs.getInt("id"));
        }

        connection.close();

    }


}
