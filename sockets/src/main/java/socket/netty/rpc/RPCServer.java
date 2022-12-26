package socket.netty.rpc;

import socket.netty.rpc.impl.MessageHeader;
import socket.netty.rpc.impl.server.ServerStatus;

import java.util.function.BiConsumer;

public interface RPCServer {

    void start();

    void stop();

    void onMessage(BiConsumer<MessageHeader, byte[]> consumer);

    ServerStatus status();

    int port();
}
