package query.sql;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import query.kv.KeyValueStore;
import query.kv.SSTable;
import query.kv.memory.InMemoryStore;
import query.tables.Order;

import java.util.*;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SQLIndexTest {

    KeyValueStore db;


    @BeforeEach
    public void createDB() {

        this.db = inMemory();
        // this.db = mvStore();
        //this.db = rocks();
    }

    @NotNull
    public InMemoryStore inMemory() {
        return new InMemoryStore();
    }

    @Test
    void select_record_using_index() {

        Map<String, Function<Order, String>> indexes = new HashMap<String, Function<Order, String>>() {{
            put("orderdate", o -> String.valueOf(o.orderDate()));

        }};

        SSTable<Order> orders = db.createTable("orders", Order.class, cols(), indexes);
        asList(
                Order.of(100, "1", 20200901, "SHIPPED", 107.6d, 5),
                Order.of(101, "2", 20200902, "SHIPPED", 967.6d, 15),
                Order.of(102, "1", 20200903, "SHIPPED", 767.6d, 25)
        ).forEach(orders::insert);

        List<Order> returnRows = new ArrayList<>();

        db.execute("select * From orders where orderdate=20200902 and status='SHIPPED' ", row -> {
            returnRows.add(Order.of(row.getLong("orderId"), row.getString("customerId"), row.getInt("orderDate"),
                    row.getString("status"), row.getDouble("amount"), row.getInt("noOfItem")));
        });

        List<Order> expectedRows = asList(
                Order.of(101, "2", 20200902, "SHIPPED", 967.6d, 15)
        );
        assertResult(expectedRows, returnRows);

    }

    private void assertResult(List<Order> expectedRows, List<Order> actualRows) {
        sort(expectedRows, Comparator.comparing(Order::orderId));
        sort(actualRows, Comparator.comparing(Order::orderId));
        assertEquals(expectedRows, actualRows);
    }


    private Map<String, Function<Order, Object>> cols() {
        Map<String, Function<Order, Object>> cols = new HashMap<String, Function<Order, Object>>() {{
            put("orderid", Order::orderId);
            put("customerid", Order::customerId);
            put("orderdate", Order::orderDate);
            put("status", Order::status);
            put("amount", Order::amount);
            put("noofitem", Order::noOfItems);
        }};
        return cols;
    }

}
