package com.org.jdbcproxy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SQLCache {
    private static final Map<String, SqlInfo> queryResults = new ConcurrentHashMap<>();

    public static List<Map<Object, Object>> newQuery(String sql) {
        List<Map<Object, Object>> result = new ArrayList<>();
        queryResults.put(sql, new SqlInfo(result));
        return result;
    }

    public static List<Map<Object, Object>> result(String sql) {
        SqlInfo sqlInfo = queryResults.get(sql);
        if (sqlInfo != null) {
            sqlInfo.hits.incrementAndGet();
            sqlInfo.lastAccessTime = LocalDateTime.now();
            return sqlInfo.rows;
        }
        return null;
    }


    public static class SqlInfo {
        public final AtomicInteger hits = new AtomicInteger();
        public LocalDateTime lastAccessTime = LocalDateTime.now();
        public final List<Map<Object, Object>> rows;


        public SqlInfo(List<Map<Object, Object>> rows) {
            this.rows = rows;
        }
    }

}
