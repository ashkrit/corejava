package com.org.jdbcproxy.filesystem;

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

public class FileSystemSQL {


    public static void createTables(Connection connection, String suffix) {

        safeExecuteV(() -> {
            String ddlQuery = buildFilesTableQuery(suffix);
            Statement statement = connection.createStatement();
            statement.executeUpdate(String.format("drop table if exists files_%s", suffix));
            statement.executeUpdate(ddlQuery);
        });
    }

    private static String buildFilesTableQuery(String suffix) {

        String columnText = tableColumns()
                .stream()
                .map(r -> String.format("%s %s", r[0], r[1]))
                .collect(Collectors.joining(", "));

        return String.format("create table files_%s (%s)", suffix, columnText);
    }

    private static List<String[]> tableColumns() {
        List<String[]> cols = new ArrayList<>();
        cols.add(new String[]{"name", "string"});
        cols.add(new String[]{"last_modified", "integer"});
        cols.add(new String[]{"size", "integer"});
        cols.add(new String[]{"is_file", "string"});
        cols.add(new String[]{"is_folder", "string"});
        cols.add(new String[]{"is_hidden", "string"});
        cols.add(new String[]{"full_path", "string"});
        return cols;
    }


    public static void loadFiles(Connection connection, String suffix, List<Path> files) {

        String colName = tableColumns().stream().map(v -> v[0]).collect(Collectors.joining(" , "));
        String colTokens = tableColumns().stream().map(v -> "?").collect(Collectors.joining(" , "));
        String insertSql = String.format("INSERT INTO files_%s (%s) values (%s)", suffix, colName, colTokens);

        try (PreparedStatement stmt = MoreLang.safeExecute(() -> connection.prepareStatement(insertSql))) {

            files.stream().map(Path::toFile).forEach(file -> {
                MoreLang.safeExecuteV(() -> {
                    stmt.clearParameters();
                    int index = 1;
                    stmt.setString(index++, file.getName());
                    stmt.setLong(index++, file.lastModified());
                    stmt.setLong(index++, file.length());
                    stmt.setBoolean(index++, file.isFile());
                    stmt.setBoolean(index++, file.isDirectory());
                    stmt.setBoolean(index++, file.isHidden());
                    stmt.setString(index++, file.getAbsolutePath());
                    stmt.executeUpdate();
                });
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
