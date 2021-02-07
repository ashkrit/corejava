package query.partition;

import java.util.HashMap;
import java.util.Map;

public class Node {
    public final String name;
    private final Map<Object, Object> values = new HashMap<>();

    public Node(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public void put(Object key, Object value) {
        values.put(key, value);
    }

    public Object get(Object key) {
        return values.get(key);
    }
}
