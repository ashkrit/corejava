package socket.netty.main;

import com.google.gson.Gson;
import socket.netty.RPCClient;
import socket.netty.MessageFormat;
import socket.netty.RPCServer;
import socket.netty.impl.client.NettyRPCClient;
import socket.netty.impl.server.NettyRPCServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class RPCClientApp {

    public static void main(String[] args) {

        String host = args[0];
        int port = Integer.parseInt(args[1]);


        RPCClient client = new NettyRPCClient(host, port);
        client.onReply(message -> System.out.println(String.format("[%s] Reply :%s", Thread.currentThread().getName(), new String(message))));


        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        reader.lines().filter(l -> !l.isEmpty()).forEach(line -> {

            client.send(line.getBytes(), MessageFormat.String);

            RequestMessage message = new RequestMessage("/ping", UUID.randomUUID().toString(), line);
            client.send(new Gson().toJson(message).getBytes(), MessageFormat.Json);
        });

    }


}
