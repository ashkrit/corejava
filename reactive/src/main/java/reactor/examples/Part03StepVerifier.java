package reactor.examples;

import org.junit.jupiter.api.Assertions;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Supplier;

public class Part03StepVerifier {
    public void expectFooBarComplete(Flux<String> flux) {
        StepVerifier.create(flux)
                .expectNext("foo", "bar")
                .verifyComplete();
    }

    public void expectFooBarError(Flux<String> flux) {
        StepVerifier.create(flux)
                .expectNext("foo", "bar")
                .verifyError(RuntimeException.class);
    }

    public void expectSkylerJesseComplete(Flux<User> just) {

        StepVerifier.create(just)
                .expectNextMatches(user -> user.getFirstName().equals("swhite"))
                .assertNext(user -> Assertions.assertEquals("jpinkman", user.getFirstName()))
                .verifyComplete();

    }


    public void expect10Elements(Flux<Long> take) {
        StepVerifier.create(take)
                .expectNextCount(10)
                .verifyComplete();
    }

    public void expect3600Elements(Supplier<Flux<Long>> o) {
        StepVerifier.withVirtualTime(o).thenAwait(Duration.ofHours(1)).expectNextCount(3600).verifyComplete();

    }
}
