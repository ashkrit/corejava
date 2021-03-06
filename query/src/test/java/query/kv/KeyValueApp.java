package query.kv;

import com.google.gson.Gson;
import query.kv.KeyValueFactory;
import query.kv.KeyValueStore;
import query.kv.SSTable;
import query.kv.TableInfo;
import query.tables.Order;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.IntStream;

public class KeyValueApp {

    static Function<Order, byte[]> toJson = row -> new Gson().toJson(row).getBytes();
    static Function<byte[], Order> fromJson = rawBytes -> new Gson().fromJson(new String(rawBytes), Order.class);
    static File tmpFile = new File(System.getProperty("java.io.tmpdir"));

    public static void main(String[] args) {
        //KeyValueStore store = create("memory://", true);
        KeyValueStore store = KeyValueFactory.create("mv:" + Paths.get(tmpFile.getAbsolutePath(), "h2db", "mvstore").toString(), true);
        //KeyValueStore store = KeyValueFactory.create("rocks:" + Paths.get(tmpFile.getAbsolutePath(), "rocks").toString(), true);


        Map<String, Function<Order, String>> indexes = new HashMap<String, Function<Order, String>>() {{
            // put("orderId", o -> String.valueOf(o.orderId()));
            //put("customerId", Order::customerId);
            //put("orderDate", o -> String.valueOf(o.orderDate()));
            //put("status", Order::status);
        }};

        AtomicLong l = new AtomicLong(System.nanoTime());
        TableInfo<Order> ordersTable = new TableInfo<>("orders", cols(), indexes, toJson, fromJson, $ -> String.valueOf(l.incrementAndGet()));

        SSTable<Order> orders = store.createTable(ordersTable);

        LocalDate start = LocalDate.now().minusDays(365);
        DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
        List<String> status = Arrays.asList("SHIPPED", "CANCELLED", "ORDERED", "PENDINGPAYMENT");

        long startTime = System.currentTimeMillis();
        IntStream
                .range(0, 1_000_000)
                .forEach(id -> {
                    int customer = ThreadLocalRandom.current().nextInt(100_000);
                    int days = ThreadLocalRandom.current().nextInt(365);
                    int orderDate = Integer.parseInt(start.plusDays(days).format(yyyyMMdd));
                    String shipped = status.get(ThreadLocalRandom.current().nextInt(status.size()));
                    orders.insert(Order.of(id, String.valueOf(customer), orderDate, shipped, 107.6d, 5));

                    if (id % 100_000 == 0) {
                        System.out.println("Loaded -> " + id);
                    }
                });

        long tot = System.currentTimeMillis() - startTime;
        store.close();
        long compactTime = System.currentTimeMillis() - startTime;

        System.out.println("Time " + tot + " Compact time " + compactTime);

    }

    private static Map<String, Function<Order, Object>> cols() {
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
