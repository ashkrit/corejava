package socket.netty;

import java.util.function.Consumer;

public interface RPCClient {

    void send(byte[] message, MessageFormat format);

    void onReply(Consumer<byte[]> consumer);
}
