package com.org.jdbcproxy.filesystem.list;

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
        cols.add(new String[]{FileSystemFields.FILE_NAME, "string"});
        cols.add(new String[]{FileSystemFields.LAST_MODIFIED_TS, "integer"});
        cols.add(new String[]{FileSystemFields.FILE_SIZE, "integer"});
        cols.add(new String[]{FileSystemFields.IS_FILE, "string"});
        cols.add(new String[]{FileSystemFields.IS_FOLDER, "string"});
        cols.add(new String[]{FileSystemFields.IS_HIDDEN, "string"});
        cols.add(new String[]{FileSystemFields.FULL_PATH, "string"});
        cols.add(new String[]{FileSystemFields.CONTENT, "content"});
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
                    stmt.setString(index++, "VIRTUAL");
                    stmt.executeUpdate();
                });
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
