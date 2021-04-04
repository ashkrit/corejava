package query.partition;

import org.jetbrains.annotations.NotNull;

import java.util.SortedMap;
import java.util.TreeMap;

public class RangePartition<K extends Comparable, V> {
    final SortedMap<K, PartitionValues<K, V>> partitions = new TreeMap<>();

    public void put(K key, V value) {
        if (partitions.isEmpty()) {
            String node = "node" + partitions.size();
            PartitionValues<K, V> values = new PartitionValues<>(node, 10);
            values.put(key, value);
            partitions.put(values.min(), values);
            return;
        }


        SortedMap<K, PartitionValues<K, V>> tail = partitions.tailMap(key);
        if (tail.isEmpty()) {
            K maxKey = partitions.lastKey();
            PartitionValues<K, V> values = partitions.get(maxKey);
            values.put(key, value);
        } else {
            K minKey = tail.firstKey();
            PartitionValues<K, V> values = partitions.get(minKey);
            values.put(key, value);
            if (key.compareTo(minKey) < 0) {
                partitions.remove(minKey);
                partitions.put(values.min(), values);
            }
        }

    }

    public PartitionValues<K, V> getPartition(K key) {
        SortedMap<K, PartitionValues<K, V>> tail = partitions.tailMap(key);
        if (tail.isEmpty()) {
            K maxKey = partitions.lastKey();
            return partitions.get(maxKey);
        } else {
            K minKey = tail.firstKey();
            return partitions.get(minKey);
        }
    }

    public V getValue(K key) {
        return getPartition(key).get(key);
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

        public K min() {
            return values.firstKey();
        }

        public K max() {
            return values.lastKey();
        }

    }


}
