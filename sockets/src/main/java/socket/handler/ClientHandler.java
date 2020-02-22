package socket.handler;

import java.io.IOException;

public interface ClientHandler<S> {
    void handle(S s) throws IOException;
}
