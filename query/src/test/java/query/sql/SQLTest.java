package query.sql;

import query.kv.KeyValueStore;
import query.kv.SSTable;
import query.tables.Order;
import org.junit.jupiter.api.Test;
import query.kv.memory.*;

import java.util.*;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SQLTest {

    KeyValueStore db = new InMemoryStore();

    @Test
    void select_all_records() {

        SSTable<Order> orders = db.createTable("orders", Order.class, cols());

        List<Order> expectedRows = asList(
                Order.of(100, "1", 20200901, "SHIPPED", 107.6d, 5),
                Order.of(101, "2", 20200902, "SHIPPED", 967.6d, 15),
                Order.of(102, "1", 20200903, "SHIPPED", 767.6d, 25)
        );
        expectedRows.forEach(orders::insert);

        List<Order> returnRows = new ArrayList<>();

        db.execute("select * From orders", row -> {
            returnRows.add(Order.of(row.getLong("orderId"), row.getString("customerId"), row.getInt("orderDate"),
                    row.getString("status"), row.getDouble("amount"), row.getInt("noOfItem")));
        });

        assertResult(expectedRows, returnRows);

    }

    @Test
    void select_records_with_limit() {

        SSTable<Order> orders = db.createTable("orders", Order.class, cols());

        asList(
                Order.of(100, "1", 20200901, "SHIPPED", 107.6d, 5),
                Order.of(101, "2", 20200902, "SHIPPED", 967.6d, 15),
                Order.of(102, "1", 20200903, "SHIPPED", 767.6d, 25)
        ).forEach(orders::insert);

        List<Order> returnRows = new ArrayList<>();

        db.execute("select * From orders limit 1", row -> {
            returnRows.add(Order.of(row.getLong("orderId"), row.getString("customerId"), row.getInt("orderDate"),
                    row.getString("status"), row.getDouble("amount"), row.getInt("noOfItem")));
        });

        assertEquals(1, returnRows.size());
    }

    @Test
    void select_rows_based_on_criteria() {

    }

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

    private void assertResult(List<Order> expectedRows, List<Order> actualRows) {
        sort(expectedRows, Comparator.comparing(Order::orderId));
        sort(actualRows, Comparator.comparing(Order::orderId));
        assertEquals(expectedRows, actualRows);
    }
}