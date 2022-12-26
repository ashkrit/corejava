package socket.netty.eventstore;

import java.util.function.Consumer;

public interface EventStore<T> {

    void publish(T event);

    void registerConsumer(String name, Consumer<T> consumer);

}
