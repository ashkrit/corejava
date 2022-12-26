package socket.netty.main;

import com.google.gson.Gson;
import socket.netty.RPCClient;
import socket.netty.MessageFormat;
import socket.netty.impl.client.NettyRPCClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.UUID;

public class RPCClientApp {

    public static void main(String[] args) throws Exception {

        String host = args[0];
        int port = Integer.parseInt(args[1]);


        RPCClient client = new NettyRPCClient(host, port);
        client.onReply(message -> System.out.println(String.format("[%s] Reply :%s", Thread.currentThread().getName(), new String(message))));


        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String hostName = InetAddress.getLocalHost().getHostAddress();

        reader.lines().filter(l -> !l.isEmpty()).forEach(line -> {

            client.send(line.getBytes(), MessageFormat.String);


            RequestMessage message = new RequestMessage(new ClientInfo(hostName, 9999), UUID.randomUUID().toString(), "/ping", line);
            client.send(new Gson().toJson(message).getBytes(), MessageFormat.Json);
        });

    }


}
