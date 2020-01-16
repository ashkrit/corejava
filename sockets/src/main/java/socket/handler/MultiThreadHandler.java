package socket.handler;

public class MultiThreadHandler<S> extends IOExceptionHandler<S> {

    public MultiThreadHandler(ClientHandler<S> handler) {
        super(handler);
    }

    @Override
    public void handle(S s) {
        new Thread(() -> super.handle(s)).start();

    }
}
