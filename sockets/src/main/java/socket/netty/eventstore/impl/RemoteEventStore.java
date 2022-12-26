package socket.netty.eventstore.impl;

import com.google.gson.Gson;
import socket.netty.eventstore.ClientInfo;
import socket.netty.eventstore.EventStore;
import socket.netty.main.RequestMessage;
import socket.netty.rpc.MessageFormat;
import socket.netty.PanicCodeExecutor;
import socket.netty.rpc.RPCServer;
import socket.netty.rpc.impl.MessageHeader;
import socket.netty.rpc.impl.client.NettyRPCClient;
import socket.netty.rpc.impl.server.NettyRPCServer;
import socket.netty.rpc.impl.server.ServerStatus;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RemoteEventStore<T> implements EventStore<T> {

    private final String remoteHost;
    private final int remotePort;
    private final String hostName;
    private final NettyRPCClient rpcClient;
    private RPCServer embedServer;
    private ClientInfo localServerClientInfo;
    private Map<String, Consumer<T>> consumers = new ConcurrentHashMap<>();
    private final Class<T> type;

    public RemoteEventStore(String remoteHost, int remotePort, Class<T> type) {
        this.type = type;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.hostName = PanicCodeExecutor.execute(() -> InetAddress.getLocalHost().getHostAddress());
        this.rpcClient = new NettyRPCClient(remoteHost, remotePort);
        startEmbeddedServer(remotePort);
        validated();

        RequestMessage message = new RequestMessage(localServerClientInfo, UUID.randomUUID().toString(), "/register", "Subscriber started");
        rpcClient.send(toJson(message), MessageFormat.Json);
    }

    private void validated() {
        if (embedServer.status() != ServerStatus.Started) {
            throw new UncheckedIOException("Unable to start server", new IOException("Port conflict"));
        }

    }

    private void startEmbeddedServer(int remotePort) {
        int nextPort = remotePort;
        for (int times = 0; times < 20; times++, ++nextPort) {

            System.out.println("Starting on " + nextPort);
            this.embedServer = new NettyRPCServer(nextPort);
            this.embedServer.onMessage(onMessageReceived());
            this.embedServer.start();
            while (embedServer.status() == ServerStatus.Init) ;
            if (embedServer.status() == ServerStatus.Started) {
                break;
            }
            if (embedServer.status() == ServerStatus.BindError) {
                System.out.println(String.format("Unable to start on port %s, trying next port", nextPort));
            }
        }
        this.localServerClientInfo = new ClientInfo(hostName, embedServer.port());
    }

    private BiConsumer<MessageHeader, byte[]> onMessageReceived() {
        return (header, rawMessage) -> {
            //System.out.println("Header :" + header);
            if (header.format == MessageFormat.String.ordinal()) {
                System.out.println("On Message :" + new String(rawMessage));
            } else {
                Gson gson = new Gson();
                RequestMessage requestMessage = gson.fromJson(new String(rawMessage), RequestMessage.class);
                System.out.println(String.format("Received Message from %s , Message Id : %s", requestMessage.clientInfo.key(), requestMessage.messageId));

                T messageToPublish = decodeMessage(requestMessage);

                consumers.forEach(($, c) -> {
                    c.accept(messageToPublish);
                });

            }
        };
    }

    private T decodeMessage(RequestMessage map) {
        Gson gson = new Gson();
        String messageBytes = gson.toJson(map.message);
        T messageToPublish = gson.fromJson(messageBytes, type);
        return messageToPublish;
    }


    @Override
    public void publish(T event) {

        RequestMessage message = new RequestMessage(localServerClientInfo, UUID.randomUUID().toString(), "/publish", event);
        rpcClient.send(toJson(message), MessageFormat.Json);

    }

    private static byte[] toJson(RequestMessage message) {
        return new Gson().toJson(message).getBytes();
    }

    @Override
    public void registerConsumer(String name, Consumer<T> consumer) {
        consumers.put(name, consumer);
    }


}
