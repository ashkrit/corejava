package com.org.jdbcproxy;

import com.org.jdbcproxy.fs.SQLFileSystemConnectionProxy;
import com.org.jdbcproxy.rdbms.SQLConnectionProxy;
import com.org.lang.MoreLang;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class SQLFactory {

    public static Map<String, SQLObjects> factory = new LinkedHashMap<>();


    static {
        register("filesystem", new SQLObjects(SQLFileSystemConnectionProxy::create) {
            @Override
            public boolean accept(String value) {
                return value.startsWith(SQLFileSystemConnectionProxy.URL_PREFIX);
            }
        });
    }


    public static void register(String name, SQLObjects sql) {
        factory.put(name, sql);
    }


    private static SQLObjects rdbms() {
        return new SQLObjects(url -> MoreLang.safeExecute(() -> {
            Connection con = DriverManager.getConnection(url);
            return SQLConnectionProxy.create(con);
        }));
    }


    public static class SQLObjects {
        public final Function<String, Connection> connection;


        public SQLObjects(Function<String, Connection> connection) {
            this.connection = connection;
        }

        public boolean accept(String $) {
            return true;
        }


    }


    public static SQLObjects search(String connectionString) {
        return factory
                .values()
                .stream()
                .filter(sql -> sql.accept(connectionString))
                .findFirst().orElse(rdbms());
    }
}
