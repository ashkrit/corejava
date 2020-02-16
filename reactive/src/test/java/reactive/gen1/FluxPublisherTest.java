package reactive.gen1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FluxPublisherTest {

    @Test
    public void finiteStream() {
        var daysStream = Flux.just(DayOfWeek.values());

        var daysOfWeek = new ArrayList<DayOfWeek>();
        daysStream.subscribe(daysOfWeek::add);

        assertEquals(List.of(DayOfWeek.values()), daysOfWeek);
    }
}
