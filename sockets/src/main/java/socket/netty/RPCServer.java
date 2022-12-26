package socket.netty;

import socket.netty.impl.MessageHandler.MessageHeader;

import java.util.function.BiConsumer;

public interface RPCServer {

    void start();

    void stop();

    void onMessage(BiConsumer<MessageHeader, byte[]> consumer);


}
