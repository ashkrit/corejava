package db;

import com.google.gson.Gson;
import db.tables.Order;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class KeyValueStoreUpdateContractTest {

    public KeyValueStore db;

    @Test
    public void insert_and_single_key_lookup() {

        TableInfo<Order> tableInfo = new TableInfo<>("orders", cols(), Collections.emptyMap(), toJson, fromJson, o -> String.valueOf(o.orderId()));

        SSTable<Order> orders = db.createTable(tableInfo);

        Order o1 = Order.of(100, "1", 20200901, "SHIPPED", 107.6d, 5);
        Order o2 = Order.of(101, "2", 20200901, "SHIPPED", 967.6d, 15);
        Order o3 = Order.of(102, "1", 20201003, "SHIPPED", 767.6d, 25);
        Order o4 = Order.of(104, "3", 20201004, "CANCEL", 767.6d, 25);

        orders.insert(o1);
        orders.insert(o2);
        orders.insert(o3);
        orders.insert(o4);

        assertAll(
                () -> assertEquals(o1, orders.get("100")),
                () -> assertEquals(o2, orders.get("101")),
                () -> assertEquals(o3, orders.get("102")),
                () -> assertEquals(o4, orders.get("104"))
        );

    }


    @Test
    public void update_key() {

        TableInfo<Order> tableInfo = new TableInfo<>("orders", cols(), Collections.emptyMap(), toJson, fromJson, o -> String.valueOf(o.orderId()));

        SSTable<Order> orders = db.createTable(tableInfo);

        Order o1 = Order.of(100, "1", 20200901, "SHIPPED", 107.6d, 5);
        Order o2 = Order.of(101, "2", 20200901, "SHIPPED", 967.6d, 15);
        Order o3 = Order.of(102, "1", 20201003, "SHIPPED", 767.6d, 25);
        Order o4 = Order.of(104, "3", 20201004, "CANCEL", 767.6d, 25);

        orders.insert(o1);
        orders.insert(o2);
        orders.insert(o3);
        orders.insert(o4);


        orders.update(Order.of(104, "3", 20201005, "SHIPPED", 767.6d, 25));

        Order newOrder = orders.get("104");

        assertAll(
                () -> assertEquals(104, newOrder.orderId()),
                () -> assertEquals("SHIPPED", newOrder.status()),
                () -> assertEquals(20201005, newOrder.orderDate())
        );

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

    Function<Order, byte[]> toJson = row -> new Gson().toJson(row).getBytes();
    Function<byte[], Order> fromJson = rawBytes -> new Gson().fromJson(new String(rawBytes), Order.class);


}
