package query.partition;

import com.google.common.hash.Funnel;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class HRWHashing<N> implements DistributedHash<N> {

    private final BiFunction<N, Object, Long> hash;
    private final ConcurrentMap<Integer, N> nodes = new ConcurrentHashMap<>();
    private final AtomicInteger index = new AtomicInteger();

    public HRWHashing(BiFunction<N, Object, Long> hash) {
        this.hash = hash;
    }


    @Override
    public void add(N node) {
        nodes.put(index.incrementAndGet(), node);
    }

    public Map<N, Long> nodes() {
        return nodes
                .entrySet()
                .stream()
                .collect(groupingBy(x -> x.getValue(), counting()));
    }

    public N assignNode(Object key) {

        long value = Long.MIN_VALUE;
        N node = null;
        for (N n : nodes.values()) {
            long newValue = hash.apply(n, key);
            if (newValue > value) {
                node = n;
                value = newValue;
            }
        }
        return node;
    }

    static <N> BiFunction<N, Object, Long> defaultHash(Funnel<N> nodeFunnel, Funnel<Object> keyFunnel, HashFunction hashFunction) {
        return (node, key) -> {
            Hasher hash = hashFunction.newHasher();
            hash.putObject(node, nodeFunnel);
            hash.putObject(key, keyFunnel);
            return hash.hash().asLong();
        };
    }
}
