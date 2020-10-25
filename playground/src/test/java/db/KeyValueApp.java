package db;

import com.google.gson.Gson;
import db.memory.InMemoryStore;
import db.persistent.mvstore.H2MVStore;
import db.persistent.rocks.RocksStore;
import db.tables.Order;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.IntStream;

public class KeyValueApp {

    static Function<Order, byte[]> toJson = row -> new Gson().toJson(row).getBytes();
    static Function<byte[], Order> fromJson = rawBytes -> new Gson().fromJson(new String(rawBytes), Order.class);

    public static void main(String[] args) {
        KeyValueStore store = new InMemoryStore();
        //KeyValueStore store = new H2MVStore(h2location("h2mv"));
        //KeyValueStore store = new RocksStore(rocksStore());


        Map<String, Function<Order, String>> indexes = new HashMap<String, Function<Order, String>>() {{
            put("orderId", o -> String.valueOf(o.orderId()));
            put("customerId", Order::customerId);
            put("orderDate", o -> String.valueOf(o.orderDate()));
            put("status", Order::status);
        }};


        TableInfo<Order> ordersTable = new TableInfo<>("orders", cols(), indexes, toJson, fromJson, $ -> String.valueOf(System.nanoTime()));

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
                });

        long tot = System.currentTimeMillis() - startTime;
        store.close();
        long compactTime = System.currentTimeMillis() - startTime;

        System.out.println("Time " + tot + " Compact time " + compactTime);

    }

    private static File rocksStore() {
        File tmpdir = new File(System.getProperty("java.io.tmpdir"), "rocks");
        System.out.println("DB created at " + tmpdir.getAbsolutePath());
        cleanFiles(tmpdir);
        return tmpdir;
    }

    private static File h2location(String h2mv) {
        File tmpdir = new File(new File(System.getProperty("java.io.tmpdir"), "mvstore"), h2mv);
        System.out.println("DB created at " + tmpdir.getAbsolutePath());
        tmpdir.getParentFile().mkdirs();
        if (tmpdir.exists()) {
            tmpdir.delete();
        }
        return tmpdir;
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

    private static void cleanFiles(File tmpdir) {
        try {
            Files.list(tmpdir.toPath()).forEach(f -> {
                try {
                    Files.deleteIfExists(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
