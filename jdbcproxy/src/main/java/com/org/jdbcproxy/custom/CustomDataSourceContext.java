package com.org.jdbcproxy.custom;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CustomDataSourceContext {

    public final Supplier<Connection> connection;
    public final Map<String, BiConsumer<Connection, RowInfo>> loadFunctions;
    public final Map<String, CollectionTable<?>> collectionLoadFunctions = new HashMap<>();

    public CustomDataSourceContext(Supplier<Connection> connection, Map<String, BiConsumer<Connection, RowInfo>> loadFunctions) {
        this.connection = connection;
        this.loadFunctions = loadFunctions;
    }


    public static class CollectionTable<V> {
        public final List<ColumnInfo> columnInfos;
        public final List<String> keys;
        public final BiFunction<String, V, Object[]> fn;
        private final Map<String, V> rawData;

        public CollectionTable(List<ColumnInfo> columnInfos, List<String> keys, BiFunction<String, V, Object[]> fn, Map<String, V> rawData) {
            this.columnInfos = columnInfos;
            this.keys = keys;
            this.fn = fn;
            this.rawData = rawData;
        }

        public Stream<Object[]> rows() {
            return this.rawData.entrySet().stream().map(entry -> fn.apply(entry.getKey(), entry.getValue()));
        }

        public static class ColumnInfo {
            public final String name;
            public final String type;

            public ColumnInfo(String name, String type) {
                this.name = name;
                this.type = type;
            }

        }
    }
}
