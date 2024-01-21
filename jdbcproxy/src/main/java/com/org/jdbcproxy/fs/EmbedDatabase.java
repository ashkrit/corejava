package com.org.jdbcproxy.fs;

import com.org.lang.MoreLang;

import java.sql.Connection;
import java.sql.DriverManager;

public class EmbedDatabase {

    private static final String CONNECTION_STRING = "jdbc:sqlite::memory:";
    static Class<?> driver = org.sqlite.JDBC.class;

    public static Connection open() {
        MoreLang.safeExecuteV(() -> Class.forName(driver.getName()));
        return MoreLang.safeExecute(() -> DriverManager.getConnection(CONNECTION_STRING));
    }

    public static Connection open(String connectionUrl) {
        MoreLang.safeExecuteV(() -> Class.forName(driver.getName()));
        return MoreLang.safeExecute(() -> DriverManager.getConnection(connectionUrl));
    }

    public static void close(Connection connection) {

    }

}
