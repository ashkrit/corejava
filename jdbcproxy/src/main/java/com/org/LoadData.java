package com.org;

import com.org.database.ext.MerchantSQL;
import com.org.jdbcproxy.SQLDriverProxy;
import com.org.jdbcproxy.SQLFactory;
import com.org.jdbcproxy.SQLFactory.SQLObjects;
import com.org.jdbcproxy.custom.CustomDataSourceContext;
import com.org.jdbcproxy.custom.RowInfo;
import com.org.jdbcproxy.custom.SQLCustomConnectionProxy;
import com.org.jdbcproxy.fs.EmbedDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class LoadData {

    public static void main(String[] args) throws Exception {
        SQLDriverProxy.register();

        Map<String, BiConsumer<Connection, RowInfo>> tables = new HashMap<>();
        tables.put("merchant", (conn, rowInfo) -> {
            MerchantSQL.createTables(conn);
            MerchantSQL.insert(conn, rowInfo);
        });

        CustomDataSourceContext context = new CustomDataSourceContext(() -> EmbedDatabase.open("jdbc:sqlite:merchant.db"), tables);

        SQLFactory.register(SQLCustomConnectionProxy.URL_PREFIX, new SQLObjects(SQLCustomConnectionProxy::create, () -> context) {
            @Override
            public boolean accept(String value) {
                return value.startsWith(SQLCustomConnectionProxy.URL_PREFIX);
            }
        });

        Connection connection = DriverManager.getConnection(SQLDriverProxy.JDBC_PROXY_KEY + SQLCustomConnectionProxy.URL_PREFIX);
        Statement statement = connection.createStatement();

        //statement.executeUpdate("insert into merchant(key) values('k1')");
        //statement.executeUpdate("insert into merchant(key) values('k2')");
        //statement.executeUpdate("insert into merchant(key) values('k3')");


        ResultSet rs = statement.executeQuery("select * from merchant order by location");
        System.out.println("Merchant" + rs);
        System.out.println("Merchant" + statement);
        while (rs.next()) {
            String name = rs.getString("name");
            String desc = rs.getString("desc");
            String location = rs.getString("location");

            System.out.println(name + "; " + desc + "; " + location);
        }
    }
}
