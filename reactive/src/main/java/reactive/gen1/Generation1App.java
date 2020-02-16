package reactive.gen1;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

public class Generation1App {

    public static void main(String[] args) throws InterruptedException {
        var stream = Flux.interval(Duration.ZERO, Duration.ofSeconds(1)).map(x -> new Random().nextInt());
        stream.subscribe(System.out::println);

        Thread.sleep(1000 * 30);
    }
}
