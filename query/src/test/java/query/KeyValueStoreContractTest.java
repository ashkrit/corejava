package query;

import query.tables.Order;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class KeyValueStoreContractTest {

    public KeyValueStore db;

    @Test
    public void create_table() {

        Map<String, Function<Order, Object>> cols = Collections.emptyMap();
        db.createTable("orders", Order.class, cols);

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

        db
                .createTable("orders", Order.class, cols);

        List<String> expectedCols = asList("orderId", "customerId", "orderDate", "status", "amount", "noOfItem");
        List<String> actualCols = db.desc("orders");

        sort(expectedCols);
        sort(actualCols);

        assertEquals(expectedCols, actualCols);
    }


    @Test
    public void insert_data() {

        SSTable<Order> orders = db.createTable("orders", Order.class, cols());

        List<Order> expectedRows = asList(
                Order.of(100, "1", 20200901, "SHIPPED", 107.6d, 5),
                Order.of(101, "2", 20200902, "SHIPPED", 967.6d, 15),
                Order.of(102, "1", 20200903, "SHIPPED", 767.6d, 25)
        );

        expectedRows.forEach(orders::insert);

        List<Order> returnRows = new ArrayList<>();
        orders.scan(returnRows::add, 5);

        assertResult(expectedRows, returnRows);
    }


    @Test
    public void table_with_single_result_index() {

        Map<String, Function<Order, String>> indexes = new HashMap<String, Function<Order, String>>() {{
            put("orderId", o -> String.valueOf(o.orderId()));
            put("customerId", Order::customerId);
            put("orderDate", o -> String.valueOf(o.orderDate()));
            put("status", Order::status);
        }};

        SSTable<Order> orders = db.createTable("orders", Order.class, cols(), indexes);

        Order o1 = Order.of(100, "1", 20200901, "SHIPPED", 107.6d, 5);
        Order o2 = Order.of(101, "2", 20200902, "SHIPPED", 967.6d, 15);
        Order o3 = Order.of(102, "1", 20200903, "SHIPPED", 767.6d, 25);
        Order o4 = Order.of(104, "3", 20200903, "CANCEL", 767.6d, 25);

        orders.insert(o1);
        orders.insert(o2);
        orders.insert(o3);
        orders.insert(o4);

        List<Order> returnRows = new ArrayList<>();
        orders.search("orderId", "100", returnRows::add, 5);

        assertResult(asList(o1), returnRows);
    }


    @Test
    public void table_with_multi_result_index() {

        Map<String, Function<Order, String>> indexes = new HashMap<String, Function<Order, String>>() {{
            put("customerId", Order::customerId);
            put("orderDate", o -> String.valueOf(o.orderDate()));
            put("status", Order::status);
        }};

        SSTable<Order> orders = db.createTable("orders", Order.class, cols(), indexes);

        Order o1 = Order.of(100, "1", 20200901, "SHIPPED", 107.6d, 5);
        Order o2 = Order.of(101, "2", 20200902, "SHIPPED", 967.6d, 15);
        Order o3 = Order.of(102, "1", 20200903, "SHIPPED", 767.6d, 25);
        Order o4 = Order.of(104, "3", 20200903, "CANCEL", 767.6d, 25);

        orders.insert(o1);
        orders.insert(o2);
        orders.insert(o3);
        orders.insert(o4);


        assertAll(
                () -> {
                    List<Order> returnRows = new ArrayList<>();
                    orders.search("status", "CANCEL", returnRows, 5);

                    assertResult(asList(o4), returnRows);
                },
                () -> {
                    List<Order> returnRows = new ArrayList<>();
                    orders.search("status", "SHIPPED", returnRows, 5);
                    assertResult(asList(o1, o2, o3), returnRows);
                },
                () -> {
                    List<Order> returnRows = new ArrayList<>();
                    orders.search("customerId", "1", returnRows, 5);
                    assertResult(asList(o1, o3), returnRows);
                },
                () -> {
                    List<Order> returnRows = new ArrayList<>();
                    orders.search("orderDate", "20200903", returnRows, 5);
                    assertResult(asList(o4, o3), returnRows);
                }
        );

    }

    @Test
    public void match_with_limit() {

        Map<String, Function<Order, String>> indexes = new HashMap<String, Function<Order, String>>() {{
            put("status", Order::status);
        }};

        SSTable<Order> orders = db.createTable("orders", Order.class, cols(), indexes);

        Order o1 = Order.of(100, "1", 20200901, "SHIPPED", 107.6d, 5);
        Order o2 = Order.of(101, "2", 20200902, "SHIPPED", 967.6d, 15);
        Order o3 = Order.of(102, "1", 20200903, "SHIPPED", 767.6d, 25);
        Order o4 = Order.of(104, "3", 20200903, "CANCEL", 767.6d, 25);

        orders.insert(o1);
        orders.insert(o2);
        orders.insert(o3);
        orders.insert(o4);


        List<Order> returnRows = new ArrayList<>();
        orders.search("status", "SHIPPED", returnRows, 2);
        assertResult(asList(o1, o2), returnRows);

    }

    @Test
    public void multi_column_index() {

        Map<String, Function<Order, String>> indexes = new HashMap<String, Function<Order, String>>() {{
            put("status_by_date", o -> o.status() + "#" + o.orderDate());
        }};

        SSTable<Order> orders = db.createTable("orders", Order.class, cols(), indexes);

        Order o1 = Order.of(100, "1", 20200901, "SHIPPED", 107.6d, 5);
        Order o2 = Order.of(101, "2", 20200901, "SHIPPED", 967.6d, 15);
        Order o3 = Order.of(102, "1", 20201003, "SHIPPED", 767.6d, 25);
        Order o4 = Order.of(104, "3", 20201004, "CANCEL", 767.6d, 25);

        orders.insert(o1);
        orders.insert(o2);
        orders.insert(o3);
        orders.insert(o4);


        assertAll(
                () -> {
                    //All Shipped
                    List<Order> returnRows = new ArrayList<>();
                    orders.search("status_by_date", "SHIPPED", returnRows, 10);
                    assertResult(asList(o1, o2, o3), returnRows);
                },
                () -> {
                    //All shipped on 202009
                    List<Order> returnRows = new ArrayList<>();
                    orders.search("status_by_date", "SHIPPED#202009", returnRows, 10);
                    assertResult(asList(o1, o2), returnRows);
                }

        );


    }

    @Test
    public void range_search() {

        Map<String, Function<Order, String>> indexes = new HashMap<String, Function<Order, String>>() {{
            put("status_by_date", o -> o.status() + "#" + o.orderDate());
        }};

        SSTable<Order> orders = db.createTable("orders", Order.class, cols(), indexes);

        Order o1 = Order.of(100, "1", 20200901, "SHIPPED", 107.6d, 5);
        Order o2 = Order.of(101, "2", 20200901, "SHIPPED", 967.6d, 15);
        Order o3 = Order.of(102, "1", 20201003, "SHIPPED", 767.6d, 25);
        Order o4 = Order.of(104, "3", 20201004, "CANCEL", 767.6d, 25);

        orders.insert(o1);
        orders.insert(o2);
        orders.insert(o3);
        orders.insert(o4);

        List<Order> returnRows = new ArrayList<>();
        orders.rangeSearch("status_by_date", "SHIPPED#20200801", "SHIPPED#20200930", returnRows, 10);

        assertResult(Arrays.asList(o1, o2), returnRows);
    }

    private void assertResult(List<Order> expectedRows, List<Order> actualRows) {
        sort(expectedRows, Comparator.comparing(Order::orderId));
        sort(actualRows, Comparator.comparing(Order::orderId));
        assertEquals(expectedRows, actualRows);
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

}
