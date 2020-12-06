package query.timeseries;


import model.avro.EventInfo;
import model.avro.LightTaxiRide;
import org.junit.jupiter.api.Test;
import query.timeseries.impl.TimeSeriesDBImpl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class TimeSeriesDBTest {

    @Test
    public void recording_failed_when_event_mapper_is_not_register() {

        TimeSeriesDB db = new TimeSeriesDBImpl();

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

        TimeSeriesDB db = new TimeSeriesDBImpl();

        LightTaxiRide ride = LightTaxiRide.newBuilder()
                .setPickupTime(System.currentTimeMillis())
                .setDropOffTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10))
                .setPassengerCount(2)
                .setTripDistance(2)
                .setTotalAmount(20)
                .build();

        db.register(LightTaxiRide.class, () -> {

            EventIdGenerator generator = new SystemTimeIdGenerator(10_000);
            AtomicInteger counter = new AtomicInteger();
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

            Function<Object, EventInfo> fn = row -> {

                LightTaxiRide value = (LightTaxiRide) row;

                String eventId = generator.next(value.getPickupTime());

                Map<CharSequence, Integer> tags = new HashMap<CharSequence, Integer>() {{
                    put("totalamount", (int) value.getTotalAmount());
                    put("date", Integer.parseInt(eventId.substring(0, 8)));
                    put("hour", Integer.parseInt(eventId.substring(8, 10)));
                }};


                return EventInfo
                        .newBuilder()
                        .setEventBody(getByteBuffer(value))
                        .setEventType("TAXIRIDE")
                        .setEventTime(eventId)
                        .setHost("NA")
                        .setService("TAXI-NO")
                        .setTags(tags)
                        .build();


            };
            return fn;
        });

        EventInfo event = db.insert(ride);
        assertNotNull(event);
        assertAll(
                () -> assertNotNull(event.getEventTime()),
                () -> assertEquals(ride, LightTaxiRide.fromByteBuffer(event.getEventBody()))
        );
    }

    private ByteBuffer getByteBuffer(LightTaxiRide value) {
        try {
            return value.toByteBuffer();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
