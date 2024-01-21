package com.org.jdbcproxy.custom;

import java.util.Map;

public class RowInfo {

    public final Map<String, Object> values;
    public final String tableName;

    public RowInfo(String tableName, Map<String, Object> values) {
        this.values = values;
        this.tableName = tableName;
    }
}
