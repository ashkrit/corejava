package socket.netty.eventstore;

import com.google.gson.Gson;
import socket.netty.main.RequestMessage;
import socket.netty.rpc.MessageFormat;
import socket.netty.rpc.RPCClient;
import socket.netty.rpc.RPCServer;
import socket.netty.rpc.impl.MessageHeader;
import socket.netty.rpc.impl.client.NettyRPCClient;
import socket.netty.rpc.impl.server.NettyRPCServer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventStoreServer {

    private final RPCServer server;
    private final Map<String, ClientInfo> clients = new ConcurrentHashMap<>();
    private Map<String, Consumer<byte[]>> messageProcessor = new HashMap<>();

    public EventStoreServer(int port) {
        this.server = new NettyRPCServer(port);
        messageProcessor.put("/register", register());
        messageProcessor.put("/publish", publish(clients));
        server.onMessage(onMessage(clients, messageProcessor));
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop();
    }

    private Consumer<byte[]> publish(Map<String, ClientInfo> clients) {
        return message -> clients.forEach((key, value) -> {
            RPCClient rpcClient = new NettyRPCClient(value.host, value.port);
            rpcClient.send(message, MessageFormat.Json);
        });
    }

    private Consumer<byte[]> register() {
        return message -> {

            RequestMessage map = decode(message);
            ClientInfo clientInfo = map.clientInfo;
            RPCClient rpcClient = new NettyRPCClient(clientInfo.host, clientInfo.port);
            rpcClient.send("Client registered".getBytes(), MessageFormat.String);

        };
    }


    private BiConsumer<MessageHeader, byte[]> onMessage(Map<String, ClientInfo> clients, Map<String, Consumer<byte[]>> messageProcessor) {

        return (header, message) -> {

            MessageFormat messageFormat = toMessageFormat(header);

            switch (messageFormat) {
                case String -> System.out.println("String Message :" + new String(message));
                case Json -> processAsJson(clients, messageProcessor, message);
            }


        };
    }

    private static void processAsJson(Map<String, ClientInfo> clients, Map<String, Consumer<byte[]>> messageProcessor, byte[] message) {
        RequestMessage requestMessage = decode(message);
        ClientInfo clientInfo = requestMessage.clientInfo;
        clients.putIfAbsent(clientInfo.key(), clientInfo);
        System.out.println(String.format("Processing : %s , message id : %s", requestMessage.action, requestMessage.messageId));
        messageProcessor.getOrDefault(requestMessage.action, noAction(requestMessage)).accept(message);
    }

    private static RequestMessage decode(byte[] message) {
        return new Gson().fromJson(new String(message), RequestMessage.class);
    }

    private static MessageFormat toMessageFormat(MessageHeader header) {
        return Arrays.stream(MessageFormat.values()).filter(format -> format.ordinal() == header.format).findAny().orElse(MessageFormat.String);
    }

    private static Consumer<byte[]> noAction(RequestMessage map) {
        return $ -> System.out.println("No mapping for action:" + map.action);
    }


}
