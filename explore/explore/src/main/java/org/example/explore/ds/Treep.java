package org.example.explore.ds;

import java.util.Iterator;
import java.util.Set;

/**
 * Data structure for 2 access pattern
 * - By Key
 * - Order by Some Attribute
 **/
public interface Treep<K, V> {
    void add(V value);

    Set<String> orderBy();

    V get(K key);

    V top(String attributeName);

    V takeTop(String attributeName);

    V remove(K key);

    Iterator<K> keys();

    int size();
}
