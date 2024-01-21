package com.org;

import com.org.jdbcproxy.SQLDriverProxy;
import com.org.jdbcproxy.SQLFactory;
import com.org.jdbcproxy.custom.SQLCustomConnectionProxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class LoadData {

    public static void main(String[] args) throws Exception {
        SQLDriverProxy.register();

        SQLFactory.register(SQLCustomConnectionProxy.URL_PREFIX, new SQLFactory.SQLObjects(SQLCustomConnectionProxy::create) {
            @Override
            public boolean accept(String value) {
                return value.startsWith(SQLCustomConnectionProxy.URL_PREFIX);
            }
        });

        Connection connection = DriverManager.getConnection(SQLDriverProxy.JDBC_PROXY_KEY + SQLCustomConnectionProxy.URL_PREFIX);
        Statement statement = connection.createStatement();
        int value = statement.executeUpdate("insert into merchant(key) values('k1')");
        System.out.println(value);
    }
}
