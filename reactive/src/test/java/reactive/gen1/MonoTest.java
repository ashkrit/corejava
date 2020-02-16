package reactive.gen1;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoTest {

    @Test
    public void single_value_mono() {

        Mono.create(sink -> sink.success("Hello"));
    }


}
