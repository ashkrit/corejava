package socket.handler;

import java.util.concurrent.ExecutorService;

public class ExecutorServiceHandler<S> extends IOExceptionHandler<S> {

    private final ExecutorService pool;
    private final Thread.UncaughtExceptionHandler errorHandler;

    public ExecutorServiceHandler(ClientHandler<S> handler, ExecutorService pool, Thread.UncaughtExceptionHandler errorHandler) {
        super(handler);
        this.pool = pool;
        this.errorHandler = errorHandler;
    }

    @Override
    public void handle(S s) {
        pool.submit(() -> execute(s));
    }

    private void execute(S s) {
        try {
            super.handle(s);
        } catch (Exception e) {
            errorHandler.uncaughtException(Thread.currentThread(), e);
        }
    }
}
