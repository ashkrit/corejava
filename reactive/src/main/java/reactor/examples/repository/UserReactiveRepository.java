package reactor.examples.repository;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.examples.User;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserReactiveRepository implements ReactiveRepository<User> {

    private final Duration delay;
    private List<User> users = new ArrayList<>();

    public UserReactiveRepository(Duration delay) {
        this.delay = delay;
    }

    public UserReactiveRepository(Duration delay, User... u) {
        this.delay = delay;
        this.users = new ArrayList<>(Arrays.asList(u));
    }

    @Override
    public Mono<Void> save(Publisher<User> value) {
        return delayMono().flatMap(c -> Mono.from(value)).map(users::add).then();
    }


    @Override
    public Mono<User> findFirst() {
        return delayMono().map(c -> users.get(0));
    }

    @Override
    public Flux<User> findAll() {
        return Flux.interval(delay)
                .zipWith(Flux.fromIterable(users), (i, users) -> users);

    }

    @Override
    public Mono<User> findById(String id) {
        var record = users.stream()
                .filter(user -> user.getFirstName().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not found" + id));
        return Mono.delay(delay).map(x -> record);
    }

    private Mono<Long> delayMono() {
        return Mono.delay(delay);
    }
}
