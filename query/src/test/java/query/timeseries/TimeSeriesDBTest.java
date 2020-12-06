package query.timeseries;


import model.avro.EventInfo;
import model.avro.LightTaxiRide;
import org.junit.jupiter.api.Test;
import query.timeseries.impl.InMemoryTimeSeries;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;

public class TimeSeriesDBTest {

    @Test
    public void recording_failed_when_event_mapper_is_not_register() {

        TimeSeriesDB db = new InMemoryTimeSeries();

        LightTaxiRide ride = LightTaxiRide.newBuilder()
                .setPickupTime(System.currentTimeMillis())
                .setDropOffTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10))
                .setPassengerCount(2)
                .setTripDistance(2)
                .setTotalAmount(20)
                .build();

        assertThrows(NullPointerException.class, () -> db.insert(ride));

    }

    @Test
    public void record_event_when_mapping_is_found() {

        TimeSeriesDB db = new InMemoryTimeSeries();

        LightTaxiRide ride = LightTaxiRide.newBuilder()
                .setPickupTime(System.currentTimeMillis())
                .setDropOffTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10))
                .setPassengerCount(2)
                .setTripDistance(2)
                .setTotalAmount(20)
                .build();

        db.register(LightTaxiRide.class, () -> {
            EventIdGenerator generator = new SystemTimeIdGenerator(10_000);
            return toEventInfo(generator);
        });

        EventInfo event = db.insert(ride);
        assertNotNull(event);
        assertAll(
                () -> assertNotNull(event.getEventTime()),
                () -> assertEquals(ride, LightTaxiRide.fromByteBuffer(event.getEventBody()))
        );
    }

    @Test
    public void allow_query_by_gt_event_time() {

        TimeSeriesDB db = new InMemoryTimeSeries();

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

        AtomicLong l = new AtomicLong();
        db.gt(LocalDateTime.now().minusDays(1), e -> {
            l.incrementAndGet();
            return true;
        });

        assertEquals(10_000, l.get());
    }


    @Test
    public void allow_query_by_gt_event_time_using_limit_signal() {

        TimeSeriesDB db = new InMemoryTimeSeries();

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

        AtomicLong l = new AtomicLong();
        db.gt(LocalDateTime.now().minusDays(1), e -> l.incrementAndGet() < 5);

        assertEquals(5, l.get());
    }


    @Test
    public void verify_gt_query_with_no_result() {

        TimeSeriesDB db = new InMemoryTimeSeries();

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

        AtomicLong l = new AtomicLong();
        db.gt(LocalDateTime.now().plusDays(10), e -> l.incrementAndGet() < 5);

        assertEquals(0, l.get());
    }


    @Test
    public void verify_lt_query() {

        TimeSeriesDB db = new InMemoryTimeSeries();

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

        AtomicLong l = new AtomicLong();
        db.lt(LocalDateTime.now().plusDays(10), e -> {
            l.incrementAndGet();
            return true;
        });

        assertEquals(10_000, l.get());
    }


    @Test
    public void verify_between_query() {

        TimeSeriesDB db = new InMemoryTimeSeries();

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

        AtomicLong l = new AtomicLong();
        db.between(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10), e -> {
            l.incrementAndGet();
            return true;
        });

        assertEquals(10_000, l.get());
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
