package faas;

import java.util.function.Function;
import java.util.function.Predicate;

public class SerCode {

    static <T, R> Function<T, R> f(SerFunction<T, R> t) {
        return t;
    }

    static <T> Predicate<T> p(SerPredicate<T> t) {
        return t;
    }
}
