## Exploration of random ideas 


#### Data structure with Map + Priority Queue Access pattern

Link to Blog - https://ashkrit.blogspot.com/2023/08/multi-indexing.html
```
public interface Treep<K, V> {
    void add(V value);

    Set<String> orderBy();

    V get(K key);

    V top(String attributeName);

    V takeTop(String attributeName);

    V delete(K key);

    Iterator<K> keys();

    int size();
}
```
