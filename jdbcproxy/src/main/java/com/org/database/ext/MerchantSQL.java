package com.org.database.ext;

import com.org.jdbcproxy.custom.RowInfo;
import com.org.jdbcproxy.fs.list.FileSystemFields;
import com.org.lang.MoreLang;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.org.lang.MoreLang.safeExecuteV;

public class MerchantSQL {

    public static String TABLE_NAME = "merchant";

    public static void createTables(Connection connection) {

        safeExecuteV(() -> {
            String ddlQuery = buildFilesTableQuery();
            Statement statement = connection.createStatement();
            System.out.println(ddlQuery);
            //statement.executeUpdate(String.format("drop table if exists %s", TABLE_NAME));
            statement.executeUpdate(ddlQuery);
        });
    }

    private static String buildFilesTableQuery() {

        String columnText = tableColumns()
                .stream()
                .map(r -> String.format("%s %s", r[0], r[1]))
                .collect(Collectors.joining(", "));

        return String.format("create table if not exists %s (%s)", TABLE_NAME, columnText);
    }

    private static List<String[]> tableColumns() {
        List<String[]> cols = new ArrayList<>();
        cols.add(new String[]{MerchantFields.NAME, "string"});
        cols.add(new String[]{MerchantFields.DESC, "string"});
        cols.add(new String[]{MerchantFields.LOCATION, "string"});
        return cols;
    }

    public static void insert(Connection connection, RowInfo rowInfo) {

        String colName = tableColumns().stream().map(v -> v[0]).collect(Collectors.joining(" , "));
        String colTokens = tableColumns().stream().map(v -> "?").collect(Collectors.joining(" , "));
        String insertSql = String.format("INSERT INTO %s (%s) values (%s)", rowInfo.tableName, colName, colTokens);

        try (PreparedStatement stmt = MoreLang.safeExecute(() -> connection.prepareStatement(insertSql))) {
            stmt.clearParameters();
            int index = 1;
            stmt.setString(index++, (String) rowInfo.values.get("key"));
            stmt.setString(index++, "TODO_" + System.nanoTime());
            stmt.setString(index++, "TODO_" + System.nanoTime());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
