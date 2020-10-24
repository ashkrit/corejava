package db;

import db.tables.Order;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KVDatabaseTest {

    @Test
    public void create_table() {
        KVDatabase db = new InMemoryKV();
        Map<String, Function<Order, Object>> cols = Collections.emptyMap();
        Table<Order> orders = db.createTable("orders", cols);

        assertEquals(Arrays.asList(), db.desc("orders"));
    }
}
