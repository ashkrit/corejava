package query.timeseries;

import model.avro.TaxiRide;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        Map<String, Integer> fields = fields(path);

        System.out.println(fields);


        Stream<String> lines = Files.lines(path);
        IntSummaryStatistics sumary = lines
                .skip(1)
                .map(r -> r.split(","))
                .mapToInt(values -> {
                    try {
                        return TaxiRide.newBuilder()
                                .setVendorId(values[fields.get("vendorid")])
                                .setPickupTime(toMilliSeconds(values[fields.get("tpep_pickup_datetime")]))
                                .setDropOffTime(toMilliSeconds(values[fields.get("tpep_dropoff_datetime")]))
                                .setPassengerCount(toInt(values[fields.get("passenger_count")]))
                                .setTripDistance(toFloat(values[fields.get("trip_distance")]))
                                .setFareAmount(toFloat(values[fields.get("fare_amount")]))
                                .setExtraAmount(toFloat(values[fields.get("extra")]))
                                .setTaxAmount(toFloat(values[fields.get("mta_tax")]))
                                .setTipAmount(toFloat(values[fields.get("tip_amount")]))
                                .setTollsAmount(toFloat(values[fields.get("tolls_amount")]))
                                .setImprovementSurcharge(toFloat(values[fields.get("improvement_surcharge")]))
                                .setTotalAmount(toFloat(values[fields.get("total_amount")]))
                                .setCongestionSurcharge(toFloat(values[fields.get("congestion_surcharge")]))
                                .build().toByteBuffer().array().length;
                    } catch (Exception e) {
                        System.out.println(Arrays.toString(values));
                        throw new RuntimeException(e);
                    }
                }).summaryStatistics();

        System.out.println(sumary);

    }

    private static Map<String, Integer> fields(Path path) throws IOException {
        Stream<String> lines = Files.lines(path);
        Collector<FieldInfo, ?, Map<String, Integer>> schemaCollector = Collectors
                .toMap(FieldInfo::getName, FieldInfo::getIndex, (f1, f2) -> f1, LinkedHashMap::new);

        Map<String, Integer> fields = lines
                .limit(1)
                .map(r -> r.split(","))
                .flatMap(r -> IntStream.range(0, r.length).mapToObj(index -> new FieldInfo(r[index].toLowerCase(), index)))
                .collect(schemaCollector);
        return fields;
    }

    static class FieldInfo {
        public final String name;
        public final int index;

        FieldInfo(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }


    private static long toMilliSeconds(String dateTime) {
        return LocalDateTime.parse(dateTime, f).toInstant(ZoneOffset.UTC).getEpochSecond();
    }

    private static LocalDateTime fromMillSeconds(long ms) {
        return LocalDateTime.ofEpochSecond(ms, 0, ZoneOffset.UTC);
    }

    private static int toInt(String value) {
        if (value.trim().isEmpty())
            return 0;
        return Integer.parseInt(value);
    }

    private static float toFloat(String value) {
        return Float.parseFloat(value);
    }

}
