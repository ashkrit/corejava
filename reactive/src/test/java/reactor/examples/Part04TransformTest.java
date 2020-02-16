package reactor.examples;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.examples.repository.ReactiveRepository;
import reactor.examples.repository.UserReactiveRepository;
import reactor.test.StepVerifier;

import java.time.Duration;

public class Part04TransformTest {

    Part04Transform workshop = new Part04Transform();
    ReactiveRepository<User> repository = new UserReactiveRepository(Duration.ofMillis(1),
            new User("User1"), new User("User2"));

    @Test
    public void transformMono() {

        Mono<User> mono = repository.findFirst();
        StepVerifier.create(workshop.capitalizeOne(mono))
                .expectNext(new User("USER1"))
                .verifyComplete();
    }

    @Test
    public void transformFlux() {

        Flux<User> flux = repository.findAll();
        StepVerifier.create(workshop.capitalizeMany(flux))
                .expectNext(new User("USER1"), new User("USER2"))
                .verifyComplete();
    }

}
