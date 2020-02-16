package reactive.gen1;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Date;

public class ExternalProducerTest {

    @Test
    public void hot_subscription() {
        var processor = EmitterProcessor.<Integer>create();

        processor.onNext(10);

        Flux<Integer> integerFlux = processor;
        integerFlux.subscribe(x -> {
            slowTask(5);
            System.out.println(new Date() + "[S1]->" + Thread.currentThread() + "->" + x);
        });
        processor.onNext(1);

        integerFlux.subscribe(x -> {
            slowTask(2);
            System.out.println(new Date() + "[S2]->" + Thread.currentThread() + "->" + x);
        });

        processor.onNext(20);
        processor.onNext(30);


    }

    private void slowTask(int seconds) {
        try {
            Thread.sleep(1000 * seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
