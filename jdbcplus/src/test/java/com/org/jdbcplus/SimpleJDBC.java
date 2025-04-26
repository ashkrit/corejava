package com.org.jdbcplus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleJDBC {

    static final Logger LOG = LoggerFactory.getLogger(SimpleJDBC.class);

    public static void main(String[] args) throws SQLException {

        String connectionString = "jdbc:sqlite:memory";
        Connection connection = DriverManager.getConnection(connectionString);

        LOG.info("Connection to SQLite has been established  {}", connection);


    }

}
