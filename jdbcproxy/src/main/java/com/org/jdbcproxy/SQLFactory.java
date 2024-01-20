package com.org.jdbcproxy;

import com.org.lang.MoreLang;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SQLFactory {

    public static Map<String, SQLObjects> factory = new HashMap<>();


    static {
        factory.put("default", new SQLObjects(url -> MoreLang.safeExecute(() -> {
            Connection con = DriverManager.getConnection(url);
            return SQLConnectionProxy.create(con);
        })));
    }


    public static class SQLObjects {
        public final Function<String, Connection> connection;


        public SQLObjects(Function<String, Connection> connection) {
            this.connection = connection;
        }
    }


}
