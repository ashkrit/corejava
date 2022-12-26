package socket.netty;

public class PanicCodeExecutor {
    public static void execute(PanicCode error) {
        try {
            error.apply();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public static interface PanicCode {
        void apply() throws Exception;
    }
}
