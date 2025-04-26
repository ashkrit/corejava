package org.database;


import com.kuzudb.Connection;
import com.kuzudb.Database;
import com.kuzudb.FlatTuple;
import com.kuzudb.QueryResult;

public class KuzuApp {
    public static void main(String[] args) throws Exception {
        // Create an in-memory database and connect to it
        Database db = new Database(":memory:");
        Connection conn = new Connection(db);
        // Create tables.
        conn.query("CREATE NODE TABLE User(name STRING, age INT64, PRIMARY KEY (name))");
        conn.query("CREATE NODE TABLE City(name STRING, population INT64, PRIMARY KEY (name))");
        conn.query("CREATE REL TABLE Follows(FROM User TO User, since INT64)");
        conn.query("CREATE REL TABLE LivesIn(FROM User TO City)");

        var basePath = "src/main/resources/graph";

        // Load data.
        conn.query("COPY User FROM '%s/user.csv'".formatted(basePath));
        conn.query("COPY City FROM '%s/city.csv'".formatted(basePath));
        conn.query("COPY Follows FROM '%s/follows.csv'".formatted(basePath));
        conn.query("COPY LivesIn FROM '%s/lives-in.csv'".formatted(basePath));

        // Execute a simple query.
        QueryResult result =
                conn.query("MATCH (a:User)-[f:Follows]->(b:User) RETURN a.name, f.since, b.name;");
        while (result.hasNext()) {
            FlatTuple row = result.getNext();
            System.out.print(row);
        }
    }
}
