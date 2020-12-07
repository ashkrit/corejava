package query.app.timeseries;

import model.avro.EventInfo;
import model.avro.TaxiRide;
import query.timeseries.id.EventIdGenerator;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TaxiRideBuilder {
    static TaxiRide createTaxiRide(Map<String, Integer> fields, String[] values) {
        try {
            return toTaxiRide(fields, values);
        } catch (Exception e) {
            System.out.println(Arrays.toString(values));
            throw new RuntimeException(e);
        }
    }

    public static TaxiRide toTaxiRide(Map<String, Integer> fields, String[] values) {
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
                .build();
    }

    public static Map<String, Integer> fields(Path path) throws IOException {
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

    public static EventInfo fromBytes(ByteBuffer b) {
        try {
            return EventInfo.fromByteBuffer(b);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Function<EventInfo, byte[]> toBytes() {
        return r -> {
            try {
                return r.toByteBuffer().array();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    public static long toMilliSeconds(String dateTime) {
        return LocalDateTime.parse(dateTime, NYTaxiRides.f).toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static int toInt(String value) {
        if (value.trim().isEmpty())
            return 0;
        return Integer.parseInt(value);
    }

    public static float toFloat(String value) {
        return Float.parseFloat(value);
    }

    public static Function<Object, EventInfo> toEventInfo(EventIdGenerator generator) {
        return row -> {
            TaxiRide value = (TaxiRide) row;
            String eventId = generator.next(value.getPickupTime());
            Map<CharSequence, Integer> tags = new HashMap<CharSequence, Integer>() {{
                put("total_amount", (int) value.getTotalAmount());
                put("date", Integer.parseInt(eventId.substring(0, 8)));
                put("hour", Integer.parseInt(eventId.substring(8, 10)));
            }};
            return EventInfo
                    .newBuilder()
                    .setEventBody(getByteBuffer(value))
                    .setEventType("TAXI_RIDE")
                    .setEventTime(eventId)
                    .setHost("NA")
                    .setService("TAXI-NO")
                    .setTags(tags)
                    .build();
        };
    }

    public static ByteBuffer getByteBuffer(TaxiRide value) {
        try {
            return value.toByteBuffer();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
