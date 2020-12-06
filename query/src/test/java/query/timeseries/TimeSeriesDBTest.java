package query.timeseries;


import model.avro.LightTaxiRide;
import org.junit.jupiter.api.Test;
import query.timeseries.impl.TimeSeriesDBImpl;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
