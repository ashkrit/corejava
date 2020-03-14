package optionals;

import java.util.Map;
import java.util.Optional;

public interface OptionalMap<K, V> extends Map<K, V> {

    default Optional<V> getValue(K key) {
        return Optional.ofNullable(get(key));
    }
}
