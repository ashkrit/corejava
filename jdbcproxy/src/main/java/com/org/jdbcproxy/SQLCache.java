package com.org.jdbcproxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SQLCache {
    private static final Map<String, List<Map<Object, Object>>> queryResults = new ConcurrentHashMap<>();

    public static List<Map<Object, Object>> newQuery(String sql) {
        List<Map<Object, Object>> result = new ArrayList<>();
        queryResults.put(sql, result);
        return result;
    }

    public static List<Map<Object, Object>> result(String sql) {
        return queryResults.get(sql);
    }

}
