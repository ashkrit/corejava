package query.partition;

import com.google.common.hash.Funnel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class HRWHashing<N, K> {

    private final Funnel<N> nodeHash;
    private final Funnel<K> keyHash;
    private ConcurrentMap<Integer, N> nodes = new ConcurrentHashMap<>();
    private AtomicInteger index = new AtomicInteger();

    public HRWHashing(Funnel<N> nodeHash, Funnel<K> keyHash) {
        this.nodeHash = nodeHash;
        this.keyHash = keyHash;
    }


    public void add(N node) {
        nodes.put(index.incrementAndGet(), node);
    }

    public Map<N, Long> nodes() {
        return nodes
                .entrySet()
                .stream()
                .collect(groupingBy(x -> x.getValue(), counting()));
    }

}
