package faas;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface SerPredicate<T> extends Predicate<T>, Serializable {
}
