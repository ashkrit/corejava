package socket.netty.main.rpc;

import com.google.gson.Gson;
import socket.netty.main.RequestMessage;
import socket.netty.rpc.MessageFormat;
import socket.netty.rpc.impl.server.NettyRPCServer;
import socket.netty.rpc.RPCServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class RPCServerApp {

    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);
        RPCServer server = new NettyRPCServer(port);


        server.onMessage((header, message) -> {

            System.out.println("Header :" + header);
            MessageFormat messageFormat = Arrays.stream(MessageFormat.values()).filter(format -> format.ordinal() == header.format).findAny().orElse(MessageFormat.String);

            switch (messageFormat) {
                case String -> System.out.println("String Message :" + new String(message));
                case Json -> {
                    RequestMessage map = new Gson().fromJson(new String(message), RequestMessage.class);
                    System.out.println("Json Message :" + new Gson().toJson(map));
                    processMessage(map);

                }
            }


        });

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

    private static void processMessage(RequestMessage message) {

    }

}
