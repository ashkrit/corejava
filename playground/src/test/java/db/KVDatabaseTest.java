package db;

import db.tables.Order;
import org.jetbrains.annotations.NotNull;
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


    @Test
    public void insert_data() {

        Table<Order> orders = db.createTable("orders", cols());

        List<Order> expectedRows = asList(
                Order.of(100, "1", 20200901, "SHIPPED", 107.6d, 5),
                Order.of(101, "2", 20200902, "SHIPPED", 967.6d, 15),
                Order.of(102, "1", 20200903, "SHIPPED", 767.6d, 25)
        );

        expectedRows.forEach(orders::insert);

        List<Order> returnRows = new ArrayList<>();
        orders.scan(5, returnRows::add);

        Collections.sort(expectedRows, Comparator.comparing(Order::orderId));
        Collections.sort(returnRows, Comparator.comparing(Order::orderId));

        assertEquals(expectedRows, returnRows);
    }

    @NotNull
    private Map<String, Function<Order, Object>> cols() {
        Map<String, Function<Order, Object>> cols = new HashMap<String, Function<Order, Object>>() {{
            put("orderId", Order::orderId);
            put("customerId", Order::customerId);
            put("orderDate", Order::orderDate);
            put("status", Order::status);
            put("amount", Order::amount);
            put("noOfItem", Order::noOfItems);
        }};
        return cols;
    }

}
