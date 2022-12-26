package socket.netty;

import socket.netty.impl.MessageHeader;
import socket.netty.impl.server.ServerStatus;

import java.util.function.BiConsumer;

public interface RPCServer {

    void start();

    void stop();

    void onMessage(BiConsumer<MessageHeader, byte[]> consumer);

    ServerStatus status();

    int port();
}
