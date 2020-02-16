package reactive.gen1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.util.ArrayList;

public class FibonacciTest {

    @Test
    public void generate_10_values() {

        var stream = Flux.generate(() -> Tuples.of(0L, 1L), (state, sink) -> {
            sink.next(state.getT1());
            return Tuples.of(state.getT2(), state.getT2() + state.getT1());
        });


        var values = new ArrayList<>();
        stream.take(10).subscribe(values::add);

        Assertions.assertEquals(34L, values.get(values.size() - 1));

    }
}
