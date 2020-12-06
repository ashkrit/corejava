package query.timeseries;

import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;

public class SSTable<V> {

    private final ConcurrentSkipListMap<String, V> events = new ConcurrentSkipListMap<>();

    public void inset(String key, V value) {
        events.put(key, value);
    }

    public void iterate(String from, String to, Function<V, Boolean> consumer) {
        if (from != null && to != null) {
            process(consumer, events.subMap(from, true, to, true));
        } else if (from != null) {
            process(consumer, events.tailMap(from, true));
        } else if (to != null) {
            process(consumer, events.headMap(to, true));
        }
    }

    public void process(Function<V, Boolean> fn, ConcurrentNavigableMap<String, V> matched) {
        for (Map.Entry<String, V> e : matched.entrySet()) {
            if (!fn.apply(e.getValue())) {
                break;
            }
        }
    }

}
