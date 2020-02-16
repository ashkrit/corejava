package reactive.gen1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.DirectProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomDataInjection {

    @Test
    public void push_custom_data_with_complete_signal() {
        var stream = DirectProcessor.<Integer>create();
        var values = new ArrayList<Integer>();
        var completed = new AtomicBoolean(false);
        stream.subscribe(values::add, System.out::println,
                () -> completed.set(true));

        stream.onNext(100);
        stream.onNext(200);
        stream.onComplete();

        assertEquals(List.of(100, 200), values);
        assertEquals(true, completed.get());
    }


    @Test
    public void push_custom_data_with_error_signal() {
        var stream = DirectProcessor.<Integer>create();

        var values = new ArrayList<Integer>();
        var error = new AtomicBoolean(false);

        stream.subscribe(values::add, e -> error.set(true));

        stream.onNext(100);
        stream.onNext(200);
        stream.onError(new IllegalArgumentException("Something went wrong"));
        stream.onNext(300);// Will ne bot sent

        assertEquals(List.of(100, 200), values);
        assertEquals(true, error.get());
    }
}
