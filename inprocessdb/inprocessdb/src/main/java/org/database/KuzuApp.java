package org.database;


import com.google.gson.GsonBuilder;
import com.kuzudb.Connection;
import com.kuzudb.Database;
import com.kuzudb.ObjectRefDestroyedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class KuzuApp {
    public static void main(String[] args) throws Exception {
        // Create an in-memory database and connect to it
        var db = new Database(":memory:");
        var conn = new Connection(db);

        // Create tables.
        createTables(conn);
        _loadData(conn);

        // Execute a simple query.
        //followers(conn);
        var query = loadQueryFromFile();

        query.query.forEach(q -> {
            try {
                System.out.println(q.name + " -> " + q.description);
                System.out.println(q.query);
                _execute(conn, q.query);
                System.out.println();
            } catch (Error e) {
                System.out.println(e.getMessage());
            }
        });

    }

    private static Queries loadQueryFromFile() throws IOException {
        var filePath = "src/main/resources/graph/samplequery.json";

        var data = new String(Files.readAllBytes(Paths.get(filePath)));
        var gson = new GsonBuilder().create();
        var queries = gson.fromJson(data, Queries.class);

        System.out.println(queries);
        return queries;
    }

    private static void _loadData(Connection conn) throws ObjectRefDestroyedException {
        var basePath = "src/main/resources/graph";

        // Load data.
        conn.query("COPY User FROM '%s/user.csv'".formatted(basePath));
        conn.query("COPY City FROM '%s/city.csv'".formatted(basePath));
        conn.query("COPY Follows FROM '%s/follows.csv'".formatted(basePath));
        conn.query("COPY LivesIn FROM '%s/lives-in.csv'".formatted(basePath));
    }

    private static void createTables(Connection conn) throws ObjectRefDestroyedException {
        conn.query("""
                CREATE NODE TABLE User(
                    name STRING,
                    age INT64,
                    PRIMARY KEY (name)
                )
                """);
        conn.query("""
                CREATE NODE TABLE City(
                    name STRING,
                    population INT64,
                    PRIMARY KEY (name)
                )
                """);
        conn.query("""
                CREATE REL TABLE Follows(
                    FROM User TO User,
                    since INT64
                )
                """);
        conn.query("""
                CREATE REL TABLE LivesIn(
                    FROM User TO City
                )
                """);
    }

    private static void followers(Connection conn) {
        // finds all instances where one user follows another user and returns the follower's name, when they started following, and the followed user's name
        String query = """
                MATCH (a:User)-[f:Follows]->(b:User)
                RETURN a.name, f.since, b.name;
                """;
        _execute(conn, query);
    }

    private static void _execute(Connection conn, String query) {

        try {
            var result = conn.query(query);
            while (result.hasNext()) {
                var row = result.getNext();
                System.out.print(row);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
