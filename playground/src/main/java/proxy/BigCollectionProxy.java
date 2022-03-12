package proxy;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BigCollectionProxy<V> implements BigCollection<V> {

    private final Supplier<BigCollection<V>> supplier;
    private final BigCollection<V> realObject;

    public BigCollectionProxy(Supplier<BigCollection<V>> supplier) {
        this.supplier = supplier;
        this.realObject = supplier.get();
    }

    @Override
    public void add(V value) {
        realObject.add(value);
    }

    @Override
    public boolean exists(V value) {
        return realObject.exists(value);
    }

    @Override
    public void forEach(Consumer<V> c) {
        realObject.forEach(c);
    }
}
