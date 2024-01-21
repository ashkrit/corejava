package com.org.jdbcproxy.custom;

import java.sql.Connection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CustomDataSourceContext {

    public final Supplier<Connection> connection;
    public final Map<String, BiConsumer<Connection, RowInfo>> tables;


    public CustomDataSourceContext(Supplier<Connection> connection, Map<String, BiConsumer<Connection, RowInfo>> tables) {
        this.connection = connection;
        this.tables = tables;
    }
}
