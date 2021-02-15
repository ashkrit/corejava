package query.partition;

public interface DistributedHash<T> {
    void add(T node);

    T assignNode(Object key);
}
