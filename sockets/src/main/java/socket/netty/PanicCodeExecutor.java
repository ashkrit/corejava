package socket.netty;

import java.util.function.Consumer;

public class PanicCodeExecutor {
    public static void execute(PanicCode error) {
        try {
            error.apply();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T execute(PanicCodeWithReturn<T> error) {
        try {
            return error.apply();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void execute(PanicCode error, Consumer<Exception> onError) {
        try {
            error.apply();
        } catch (Exception e) {
            onError.accept(e);
        }
    }

    @FunctionalInterface
    public static interface PanicCode {
        void apply() throws Exception;
    }

    @FunctionalInterface
    public static interface PanicCodeWithReturn<T> {
        T apply() throws Exception;
    }
}
