package org.example.explore.ds;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapPriorityQueue<K,V> implements Treep<K, V> {
    public final Map<K, V> items;
    public final Map<String, PriorityQueue<V>> orderedItems;

    private final Function<V, K> toKey;

    public MapPriorityQueue(Function<V, K> toKey, Map<String, Comparator<V>> orderBy) {
        this.items = new HashMap<>();
        this.orderedItems = orderBy
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new PriorityQueue<>(entry.getValue())));

        this.toKey = toKey;
    }

    @Override
    public void add(V value) {
        items.put(toKey.apply(value), value);
        orderedItems.forEach(($, v) -> v.add(value));
    }

    @Override
    public Set<String> orderBy() {
        return orderedItems.keySet();
    }

    @Override
    public V get(K value) {
        return items.get(value);
    }


    @Override
    public V top(String attributeName) {
        PriorityQueue<V> item = loadPQ(attributeName);
        return item.peek();
    }

    private PriorityQueue<V> loadPQ(String attributeName) {
        return this.orderedItems.get(attributeName);
    }

    @Override
    public V takeTop(String attributeName) {

        PriorityQueue<V> vs = loadPQ(attributeName);
        V item = vs.poll();

        if (item != null) {
            K derivedKey = toKey.apply(item);
            _delete(derivedKey, item);
        }
        return item;
    }

    private void _delete(K key, V item) {
        items.remove(key);
        orderedItems.forEach(($, v) -> {
            v.remove(item);
        });
    }

    @Override
    public V delete(K key) {
        V value = items.remove(key);
        _delete(key, value);
        return value;
    }


    @Override
    public Iterator<K> keys() {
        return items.keySet().iterator();
    }

    @Override
    public int size() {
        return items.size();
    }
}
