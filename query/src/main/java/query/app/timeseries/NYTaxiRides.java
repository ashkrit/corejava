package query.app.timeseries;

import model.avro.EventInfo;
import model.avro.TaxiRide;
import org.jetbrains.annotations.NotNull;
import query.timeseries.TimeSeriesStore;
import query.timeseries.id.EventIdGenerator;
import query.timeseries.id.SystemTimeIdGenerator;
import query.timeseries.sst.SortedStringTable;
import query.timeseries.sst.disk.PersistentSSTable;
import query.timeseries.sst.disk.RecordSerializer;
import query.timeseries.sst.disk.StoreLocation;
import query.timeseries.sst.memory.InMemorySSTable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * Data Source : https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page
 * <p>
 * NY - https://s3.amazonaws.com/nyc-tlc/trip+data/yellow_tripdata_2020-01.csv
 */
public class NYTaxiRides {

    static DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {

        String fileToRead = args[0];

        Path path = Paths.get(fileToRead);
        Map<String, Integer> fields = TaxiRideBuilder.fields(path);

        System.out.println(fields);

        ExecutorService es = Executors.newFixedThreadPool(1);
        TimeSeriesStore store = TimeSeriesStore.persistence(createPersistenceStore());
        store.register(TaxiRide.class, () -> {
            EventIdGenerator generator = new SystemTimeIdGenerator(10_000);
            return TaxiRideBuilder.toEventInfo(generator);
        });

        insert(path, fields, es, store);
        System.out.println("Read to accept query");
        new BufferedReader(new InputStreamReader(System.in))
                .lines()
                .filter(line -> !line.trim().isEmpty())
                .forEach(line -> {
                    try {
                        processCommand(store, line);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private static void processCommand(TimeSeriesStore store, String line) {
        String[] parts = line.split(" ");
        int limit = Integer.parseInt(parts[0]);
        AtomicInteger counter = new AtomicInteger();
        Function<EventInfo, Boolean> processor = r -> {
            counter.incrementAndGet();
            if (counter.get() < limit) {
                System.out.println(r);
            }
            return true;
        };

        String op = parts[1];
        long start = System.currentTimeMillis();
        executeQuery(store, parts, op, processor);
        long total = System.currentTimeMillis() - start;
        System.out.println(String.format("Query %s , Count %s tool %s ms", line, counter.get(), total));
    }

    @NotNull
    private static SortedStringTable<EventInfo> createPersistenceStore() {
        File storeLocation = new File(System.getProperty("java.io.tmpdir"), "events-reads-full");
        storeLocation.mkdirs();
        RecordSerializer<EventInfo> recordSerializer = new RecordSerializer<>(1024 * 8,
                TaxiRideBuilder.toBytes(), TaxiRideBuilder::fromBytes, e -> e.getEventTime().toString());
        return new PersistentSSTable<>(new InMemorySSTable<>(10_000),
                new StoreLocation(storeLocation, "taxi_events"), recordSerializer);
    }

    private static void executeQuery(TimeSeriesStore store, String[] parts, String op, Function<EventInfo, Boolean> processor) {
        DateTimeFormatter datePattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        if (op.equals("bt")) {
            LocalDateTime start = LocalDateTime.parse(parts[2], datePattern);
            LocalDateTime end = LocalDateTime.parse(parts[3], datePattern);
            store.between(start, end, processor);
        } else if (op.equals("gt")) {
            LocalDateTime start = LocalDateTime.parse(parts[2], datePattern);
            store.gt(start, processor);
        } else if (op.equals("lt")) {
            LocalDateTime start = LocalDateTime.parse(parts[2], datePattern);
            store.lt(start, processor);
        }
    }

    private static void insert(Path path, Map<String, Integer> fields, ExecutorService es, TimeSeriesStore store) {
        AtomicInteger recordCounter = new AtomicInteger();
        Stream<String> lines = lines(path);
        AtomicInteger flushRequest = new AtomicInteger();
        lines
                .skip(1)
                .skip((100_000 * 10) * 7)
                .limit(100_000 * 10)
                .map(r -> r.split(","))
                .map(values -> TaxiRideBuilder.createTaxiRide(fields, values))
                .forEach(ride -> {
                    store.insert(ride);
                    int value = recordCounter.incrementAndGet();
                    flushResult(es, store, value, flushRequest);
                });
        System.out.println("Loaded - " + recordCounter + " Records ");
    }

    private static void flushResult(ExecutorService es, TimeSeriesStore store, int value, AtomicInteger flushRequest) {
        if (value % 10_000 == 0) {
            flushRequest.incrementAndGet();
            runAsync(() -> {
                long start = System.currentTimeMillis();
                System.out.println("Flush starting ->" + store);
                store.flush();
                long total = System.currentTimeMillis() - start;
                int pending = flushRequest.decrementAndGet();
                System.out.println("Flush Done -> " + store + " Took " + total + " pending request " + pending);
            }, es);

        }
    }

    private static Stream<String> lines(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
