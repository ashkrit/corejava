package query.partition;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class ConsistentHashing<T> implements DistributedHash<T> {

    private final Function<byte[], Integer> hashFunction;
    private final int replica;
    private final SortedMap<Integer, T> ring = new TreeMap<>();
    private final Function<T, String> nodeKey;

    public ConsistentHashing(Function<byte[], Integer> hashFunction, int replica, Function<T, String> nodeKey) {
        this.hashFunction = hashFunction;
        this.replica = replica;
        this.nodeKey = nodeKey;
    }

    @Override
    public void add(T node) {
        for (int c = 0; c < replica; c++) {
            String key = String.format("%s_%s", nodeKey.apply(node), c);
            ring.put(hashFunction.apply(key.getBytes()), node);
        }
    }

    @Override
    public T findSlot(Object key) {
        int hash = hashFunction.apply(key.toString().getBytes());
        return ring.getOrDefault(hash, findClosestSlot(hash));
    }

    private T findClosestSlot(int hash) {
        SortedMap<Integer, T> tail = ring.tailMap(hash);
        int keyHash = tail.isEmpty() ? ring.firstKey() : tail.firstKey();
        return ring.get(keyHash);
    }

    public Map<T, Long> nodes() {
        return ring
                .entrySet()
                .stream()
                .collect(groupingBy(x -> x.getValue(), counting()));
    }
}
