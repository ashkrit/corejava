package reactor.examples.repository;

import reactor.core.publisher.Mono;
import reactor.examples.User;

import java.time.Duration;

public class UserRepository implements BlockingRepository<User> {

    private final UserReactiveRepository repository;

    public UserRepository(Duration delay) {
        this.repository = new UserReactiveRepository(delay);
    }

    @Override
    public void save(User value) {
        repository.save(Mono.just(value)).block();
    }

    @Override
    public User findFirst() {
        return repository.findFirst().block();
    }

    @Override
    public Iterable<User> findAll() {
        return repository.findAll().toIterable();
    }

    @Override
    public User findById(String id) {
        return repository.findById(id).block();
    }

}
