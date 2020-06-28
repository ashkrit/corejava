package faas;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface SerFunction<T, R> extends Function<T, R>, Serializable {
}
