package context.impl;


import context.ContextProviderClient;
import context.DistributedContextBuilder;
import org.h2.Driver;

import java.sql.*;
import java.util.Map;

public class DBContextProviderClient implements ContextProviderClient {

    private final String jdbcURL;
    private final Connection connection;


    public DBContextProviderClient(String dbName) {
        Driver.load();
        this.jdbcURL = String.format("jdbc:h2:%s", dbName);
        this.connection = openConnect();

        try (Connection connection = openConnect()) {
            prepareSchema(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void prepareSchema(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        statement.execute("CREATE TABLE IF NOT EXISTS context_meta(name VARCHAR,ttl LONG,ping_time LONG); ");
        statement.execute("CREATE UNIQUE INDEX IF NOT EXISTS context_meta_name ON context_meta(name);");

        statement.execute("CREATE TABLE IF NOT EXISTS context_number(context_name VARCHAR,var_name VARCHAR,var_value LONG);");
        statement.execute("CREATE UNIQUE INDEX IF NOT EXISTS context_number_n_v ON context_number(context_name,var_name); ");

        statement.execute("CREATE TABLE IF NOT EXISTS context_maps(context_name VARCHAR,map_name VARCHAR,var_name VARCHAR,var_value VARCHAR);");
        statement.execute("CREATE UNIQUE INDEX IF NOT EXISTS context_maps_n_v ON context_maps(context_name,map_name,var_name); ");
    }

    public Connection openConnect() {
        try {
            return DriverManager.getConnection(jdbcURL, "", "");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean createOrUseContext(String name, DistributedContextBuilder.InternalDistributedContext context) {
        try (Connection connection = openConnect()) {
            if (updateContext(context, connection)) {
                return true;
            } else if (insertContext(context, connection)) {
                return true;
            } else {
                updateContext(context, connection);
            }
            connection.commit();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private boolean insertContext(DistributedContextBuilder.InternalDistributedContext context, Connection connection) throws SQLException {
        int index = 0;
        String insert = "INSERT INTO context_meta(name, ttl  , ping_time) VALUES(?,?,?)";
        PreparedStatement insertStatement = connection.prepareStatement(insert);
        insertStatement.setString(++index, context.name);
        insertStatement.setLong(++index, context.ttlInMinutes);
        insertStatement.setLong(++index, System.currentTimeMillis());

        try {
            insertStatement.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        }

    }

    private boolean updateContext(DistributedContextBuilder.InternalDistributedContext context, Connection connection) throws SQLException {
        String update = "UPDATE context_meta SET ttl = ? , ping_time = ? WHERE name = ?";

        PreparedStatement updateStatement = connection.prepareStatement(update);
        int index = 0;
        updateStatement.setLong(++index, context.ttlInMinutes);
        updateStatement.setLong(++index, System.currentTimeMillis());
        updateStatement.setString(++index, context.name);

        return updateStatement.executeUpdate() > 0;
    }

    @Override
    public long longValue(String contextName, String varName) {
        try (Connection connection = openConnect()) {

            String select = "SELECT var_value FROM context_number WHERE context_name =? and var_name = ?";
            PreparedStatement selectStatement = connection.prepareStatement(select);
            int index = 0;
            selectStatement.setString(++index, contextName);
            selectStatement.setString(++index, varName);

            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public boolean longValueCas(String contextName, String variable, long current, long newValue) {

        try (Connection connection = openConnect()) {

            if (longValue(contextName, variable) == 0) {
                if (insertNumber(contextName, variable, newValue, connection)) {
                    return true;
                } else {
                    return false;
                }
            }
            return updateNumberCas(contextName, variable, current, newValue, connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean insertNumber(String contextName, String variable, long newValue, Connection connection) throws SQLException {
        String insert = "INSERT INTO context_number(context_name, var_name , var_value) VALUES(?,?,?)";
        PreparedStatement insertStatement = connection.prepareStatement(insert);
        int index = 0;
        insertStatement.setString(++index, contextName);
        insertStatement.setString(++index, variable);
        insertStatement.setLong(++index, newValue);
        try {
            return insertStatement.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        }
    }

    private static boolean updateNumberCas(String contextName, String variable, long current, long newValue, Connection connection) throws SQLException {
        String update = "UPDATE context_number set var_value = ? WHERE context_name =? and var_name = ? and var_value=?";
        PreparedStatement selectStatement = connection.prepareStatement(update);
        int index = 0;
        selectStatement.setLong(++index, newValue);
        selectStatement.setString(++index, contextName);
        selectStatement.setString(++index, variable);
        selectStatement.setLong(++index, current);

        return selectStatement.executeUpdate() > 0;
    }

    @Override
    public String get(String contextName, String mapName, String keyName) {
        try (Connection connection = openConnect()) {

            String select = "SELECT var_value FROM context_maps WHERE context_name =?  and map_name = ? and var_name =? ";
            PreparedStatement selectStatement = connection.prepareStatement(select);
            int index = 0;
            selectStatement.setString(++index, contextName);
            selectStatement.setString(++index, mapName);
            selectStatement.setString(++index, keyName);

            ResultSet rs = selectStatement.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public String remove(String contextName, String mapName, String keyName) {

        String currentValue = get(contextName, mapName, keyName);
        try (Connection connection = openConnect()) {

            String select = "DELETE FROM context_maps WHERE context_name =?  and map_name = ? and var_name =? ";
            PreparedStatement selectStatement = connection.prepareStatement(select);
            int index = 0;
            selectStatement.setString(++index, contextName);
            selectStatement.setString(++index, mapName);
            selectStatement.setString(++index, keyName);

            selectStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currentValue;
    }

    @Override
    public String put(String contextName, String mapName, Map.Entry<String, String> entry) {
        try (Connection connection = openConnect()) {

            if (updateMapKey(contextName, mapName, entry, connection, null)) {
                return entry.getValue();
            } else if (insertMapKey(contextName, mapName, entry, connection)) {
                return entry.getValue();
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public boolean casPut(String contextName, String mapName, Map.Entry<String, String> entry, String oldValue) {
        try (Connection connection = openConnect()) {

            if (updateMapKey(contextName, mapName, entry, connection, oldValue)) {
                return true;
            } else if (insertMapKey(contextName, mapName, entry, connection)) {
                return true;
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    private boolean insertMapKey(String contextName, String mapName, Map.Entry<String, String> entry, Connection connection) throws SQLException {
        int index = 0;
        String insert = "INSERT INTO context_maps(context_name, map_name , var_name,var_value) VALUES(?,?,?,?)";
        PreparedStatement insertStatement = connection.prepareStatement(insert);
        insertStatement.setString(++index, contextName);
        insertStatement.setString(++index, mapName);
        insertStatement.setString(++index, entry.getKey());
        insertStatement.setString(++index, entry.getValue());
        try {
            insertStatement.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        }

    }

    private boolean updateMapKey(String contextName, String mapName, Map.Entry<String, String> entry, Connection connection, String oldValue) throws SQLException {


        int index = 0;

        String sql = "UPDATE context_maps SET var_value = ? WHERE context_name =? AND map_name =? AND var_name = ?";

        if (oldValue != null) {
            sql += " AND var_value= ? ";
        }

        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(++index, entry.getValue());
        statement.setString(++index, contextName);
        statement.setString(++index, mapName);
        statement.setString(++index, entry.getKey());

        if (oldValue != null) {
            statement.setString(++index, oldValue);
        }

        return statement.executeUpdate() > 0;

    }
}
