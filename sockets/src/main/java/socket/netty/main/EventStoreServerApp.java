package socket.netty.main;

import com.google.gson.Gson;
import socket.netty.MessageFormat;
import socket.netty.RPCClient;
import socket.netty.RPCServer;
import socket.netty.impl.MessageHeader;
import socket.netty.impl.client.NettyRPCClient;
import socket.netty.impl.server.NettyRPCServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventStoreServerApp {

    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);
        RPCServer server = new NettyRPCServer(port);
        Map<String, ClientInfo> clients = new ConcurrentHashMap<>();
        Map<String, Consumer<byte[]>> messageProcessor = new HashMap<>();

        messageProcessor.put("/register", register());
        messageProcessor.put("/publish", publish(clients));

        server.onMessage(onMessage(clients, messageProcessor));

        server.start();

        System.out.println("Type close to disconnect");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines().filter(li -> !li.isEmpty()).forEach(cmd -> {
            if (cmd.equalsIgnoreCase("stop")) {
                server.stop();
            } else {
                System.out.println("Not supported command " + cmd);
            }
        });
    }

    private static BiConsumer<MessageHeader, byte[]> onMessage(Map<String, ClientInfo> clients, Map<String, Consumer<byte[]>> messageProcessor) {

        return (header, message) -> {

            System.out.println("Header :" + header);
            MessageFormat messageFormat = Arrays.stream(MessageFormat.values()).filter(format -> format.ordinal() == header.format).findAny().orElse(MessageFormat.String);

            switch (messageFormat) {
                case String -> System.out.println("String Message :" + new String(message));
                case Json -> {
                    RequestMessage map = new Gson().fromJson(new String(message), RequestMessage.class);
                    System.out.println("Json Message :" + new Gson().toJson(map));
                    ClientInfo clientInfo = map.clientInfo;
                    clients.putIfAbsent(clientInfo.key(), clientInfo);

                    System.out.println(String.format("Processing : %s , message id : %s", map.action, map.messageId));
                    Consumer<byte[]> messageCOnsumer = messageProcessor.getOrDefault(map.action, $ -> System.out.println("No mapping for action:" + map.action));

                    messageCOnsumer.accept(message);

                }
            }


        };
    }

    private static Consumer<byte[]> publish(Map<String, ClientInfo> clients) {
        return message -> clients.forEach((key, value) -> {
            RPCClient rpcClient = new NettyRPCClient(value.host, value.port);
            rpcClient.send(message, MessageFormat.Json);
        });
    }

    private static Consumer<byte[]> register() {
        return message -> {

            RequestMessage map = new Gson().fromJson(new String(message), RequestMessage.class);
            ClientInfo clientInfo = map.clientInfo;
            RPCClient rpcClient = new NettyRPCClient(clientInfo.host, clientInfo.port);
            rpcClient.send("Client registered".getBytes(), MessageFormat.String);

        };
    }

}
