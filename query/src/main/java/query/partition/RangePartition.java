package query.partition;

import org.jetbrains.annotations.NotNull;

import java.util.SortedMap;
import java.util.TreeMap;

public class RangePartition<K extends Comparable, V> {
    final SortedMap<RangeKey<K>, PartitionValues<K, V>> partitions = new TreeMap<>();

    public void put(K key, V value) {
        if (partitions.isEmpty()) {
            String node = "node" + partitions.size();
            PartitionValues<K, V> values = new PartitionValues<>(node, 10);
            values.put(key, value);
            partitions.put(values.rangeKey(), values);
        }

    }

    public PartitionValues<K, V> get(K key) {
        if (partitions.size() == 1) {
            return partitions.get(partitions.lastKey());
        }
        return null;
    }

    static class PartitionValues<K extends Comparable, V> {
        final String name;
        final int capacity;
        final SortedMap<K, V> values = new TreeMap<>();

        PartitionValues(String name, int capacity) {
            this.name = name;
            this.capacity = capacity;
        }

        public V get(K key) {
            return values.get(key);
        }

        public void put(K key, V value) {
            values.put(key, value);
        }

        public RangeKey<K> rangeKey() {
            return new RangeKey<>(values.firstKey(), values.lastKey());
        }

    }

    public static class RangeKey<K extends Comparable> implements Comparable<RangeKey> {
        final K start;
        final K end;

        RangeKey(K start, K end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public int compareTo(@NotNull RangeKey o) {
            return o.start.compareTo(start);
        }
    }

}
