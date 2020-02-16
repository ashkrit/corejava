package reactor.examples;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Part04Transform {


    public Mono<User> capitalizeOne(Mono<User> mono) {
        return mono.map(u -> new User(u.getFirstName().toUpperCase()));
    }

    public Flux<User> capitalizeMany(Flux<User> flux) {
        return flux.map(u -> new User(u.getFirstName().toUpperCase()));
    }
}
