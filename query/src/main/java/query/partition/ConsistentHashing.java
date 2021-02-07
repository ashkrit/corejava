package query.partition;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class ConsistentHashing<T> {


    private final Function<byte[], Integer> hashFunction;
    private final int replica;
    private final NavigableMap<Integer, T> ring = new TreeMap<>();
    private final Function<T, String> nodeKey;

    public ConsistentHashing(Function<byte[], Integer> hashFunction, int replica, Function<T, String> nodeKey) {
        this.hashFunction = hashFunction;
        this.replica = replica;
        this.nodeKey = nodeKey;
    }

    public void add(T node) {
        for (int c = 0; c < replica; c++) {
            String key = String.format("%s_%s", nodeKey.apply(node), c);
            ring.put(hashFunction.apply(key.getBytes()), node);
        }
    }

    public Map<T, Long> nodes() {

        return ring
                .entrySet()
                .stream()
                .collect(groupingBy(x -> x.getValue(), counting()));
    }
}
