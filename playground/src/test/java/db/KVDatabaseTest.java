package db;

import db.tables.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class KVDatabaseTest {

    KVDatabase db;

    @BeforeEach
    public void createDB() {
        this.db = new InMemoryKV();
    }

    @Test
    public void create_table() {

        Map<String, Function<Order, Object>> cols = Collections.emptyMap();
        db.createTable("orders", cols);

        assertEquals(asList(), db.desc("orders"));
    }


    @Test
    public void create_table_with_cols() {


        Map<String, Function<Order, Object>> cols = new HashMap<String, Function<Order, Object>>() {{
            put("orderId", Order::orderId);
            put("customerId", Order::customerId);
            put("orderDate", Order::orderDate);
            put("status", Order::status);
            put("amount", Order::amount);
            put("noOfItem", Order::noOfItems);
        }};
        db.createTable("orders", cols);

        List<String> expectedCols = asList("orderId", "customerId", "orderDate", "status", "amount", "noOfItem");
        List<String> actualCols = db.desc("orders");

        Collections.sort(expectedCols);
        Collections.sort(actualCols);

        assertEquals(expectedCols, actualCols);
    }
}
