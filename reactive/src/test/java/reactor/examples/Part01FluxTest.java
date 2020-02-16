package reactor.examples;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class Part01FluxTest {

    Part01Flux workshop = new Part01Flux();

    @Test
    public void empty() {
        Flux<String> flux = workshop.emptyFlux();
        StepVerifier.create(flux).verifyComplete();
    }

    @Test
    public void fromValues() {
        Flux<String> flux = workshop.fooBarFluxFromValues();
        StepVerifier.create(flux)
                .expectNext("foo", "bar")
                .verifyComplete();
    }

    @Test
    public void fromList() {
        Flux<String> flux = workshop.fooBarFluxFromList();
        StepVerifier.create(flux)
                .expectNext("foo", "bar")
                .verifyComplete();
    }

    @Test
    public void error() {
        Flux<String> flux = workshop.errorFlux();
        StepVerifier.create(flux)
                .verifyError(IllegalStateException.class);
    }

    @Test
    public void countEach100ms() {
        Flux<Long> flux = workshop.counter();
        StepVerifier.create(flux)
                .expectNext(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)
                .verifyComplete();
    }
}
