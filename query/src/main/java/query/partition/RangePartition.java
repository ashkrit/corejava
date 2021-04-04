package query.partition;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RangePartition<K extends Comparable, V> {
    final AtomicInteger nodeIndex = new AtomicInteger();
    final SortedMap<K, PartitionValues<K, V>> partitions = new TreeMap<>();
    private final int limit;

    public RangePartition(int limit) {
        this.limit = limit;
    }

    public RangePartition() {
        this(10);
    }

    public void put(K key, V value) {
        if (partitions.isEmpty()) {
            String node = "node-" + nodeIndex.incrementAndGet();
            PartitionValues<K, V> values = new PartitionValues<>(node, limit);
            values.put(key, value);
            partitions.put(values.min(), values);
            return;
        }

        SortedMap<K, PartitionValues<K, V>> tail = partitions.headMap(key);
        K pKey = tail.isEmpty() ? partitions.firstKey() : tail.lastKey();

        PartitionValues<K, V> node = partitions.get(pKey);
        node.put(key, value);

        if (node.min() != pKey) {
            partitions.remove(pKey);
            partitions.put(node.min(), node);
        }

        if (node.splitRequired()) {
            PartitionValues<K, V> secondHalfNode = node.split("node-" + nodeIndex.incrementAndGet());
            partitions.put(secondHalfNode.min(), secondHalfNode);
        }
    }

    public PartitionValues<K, V> partition(K key) {
        SortedMap<K, PartitionValues<K, V>> tail = partitions.headMap(key);
        if (tail.isEmpty()) {
            K maxKey = partitions.firstKey();
            return partitions.get(maxKey);
        } else {
            K minKey = tail.lastKey();
            return partitions.get(minKey);
        }
    }

    public V value(K key) {
        return partition(key).get(key);
    }

    static class PartitionValues<K extends Comparable, V> {

        final String name;
        final int capacity;
        final SortedMap<K, V> values = new TreeMap<>();

        public PartitionValues(String name, int capacity) {
            this.name = name;
            this.capacity = capacity;
        }

        public V get(K key) {
            return values.get(key);
        }

        public void put(K key, V value) {
            values.put(key, value);
        }

        public K min() {
            return values.firstKey();
        }

        public K max() {
            return values.lastKey();
        }

        public boolean splitRequired() {
            return values.size() > capacity;
        }

        public PartitionValues<K, V> split(String nodeId) {
            PartitionValues<K, V> splitNode = new PartitionValues<>(nodeId, capacity);

            int mid = values.size() / 2;
            List<K> ks = new ArrayList<>(values.keySet()).subList(mid, values.size());
            ks.forEach(k -> splitNode.put(k, values.remove(k)));

            return splitNode;
        }
    }


}
