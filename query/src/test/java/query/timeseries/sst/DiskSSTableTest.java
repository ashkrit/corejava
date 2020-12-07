package query.timeseries.sst;

import model.avro.EventInfo;
import model.avro.LightTaxiRide;
import org.junit.jupiter.api.Test;
import query.timeseries.id.EventIdGenerator;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DiskSSTableTest {

    @Test
    public void stores_pages_to_disk_and_drain_read_buffer() {

        /*
        File storeLocation = new File(System.getProperty("java.io.tmpdir"), "events");
        storeLocation.mkdirs();

        SortedStringTable<EventInfo> store = new DiskSSTable<>(new InMemorySSTable<>(10), storeLocation, "taxi_events");
        TimeSeriesStore db = new BasicTimeSeriesDatabase(store);

        db.register(LightTaxiRide.class, () -> {
            EventIdGenerator generator = new SystemTimeIdGenerator(10_000);
            return toEventInfo(generator);
        });

        range(0, 10_000).mapToObj(t -> {
            long now = System.currentTimeMillis();
            long pickTime = now + TimeUnit.MINUTES.toMillis(t);
            return LightTaxiRide.newBuilder()
                    .setPickupTime(pickTime)
                    .setDropOffTime(pickTime + TimeUnit.MINUTES.toMillis(ThreadLocalRandom.current().nextInt(50)))
                    .setPassengerCount(2)
                    .setTripDistance(2)
                    .setTotalAmount(20)
                    .build();
        }).forEach(db::insert);

        store.flush();

        assertAll(
                () -> assertEquals(0, store.buffers().size()),
                () -> assertTrue(Paths.get(storeLocation.getAbsolutePath(), "taxi_events.1.index").toFile().length() < 0),
                () -> assertTrue(Paths.get(storeLocation.getAbsolutePath(), "taxi_events.1.data").toFile().length() < 0)
        );

         */
    }

    private Function<Object, EventInfo> toEventInfo(EventIdGenerator generator) {
        return row -> {
            LightTaxiRide value = (LightTaxiRide) row;
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

    private ByteBuffer getByteBuffer(LightTaxiRide value) {
        try {
            return value.toByteBuffer();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
