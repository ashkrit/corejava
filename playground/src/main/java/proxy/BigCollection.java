package proxy;

import java.util.function.Consumer;

public interface BigCollection<V> {
    void add(V value);

    boolean exists(V value);

    void forEach(Consumer<V> c);
}
