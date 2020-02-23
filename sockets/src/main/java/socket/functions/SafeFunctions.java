package socket.functions;

import java.util.function.Function;

public class SafeFunctions {

    public static <T, R> Function<T, R> toUnChecked(CheckedFunction<T, R> f) {

        return (T input) -> {
            try {
                return f.apply(input);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
