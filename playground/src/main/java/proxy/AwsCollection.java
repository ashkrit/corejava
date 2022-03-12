package proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AwsCollection<V> implements BigCollection<V> {
    private List<V> collection = new ArrayList<>();

    @Override
    public void add(V value) {
        collection.add(value);
    }

    @Override
    public boolean exists(V value) {
        return collection.contains(value);
    }

    @Override
    public void forEach(Consumer<V> c) {
        collection.forEach(c::accept);
    }
}
