package socket.netty.main;

import com.google.gson.Gson;
import socket.netty.MessageFormat;
import socket.netty.impl.server.NettyRPCServer;
import socket.netty.RPCServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

public class RPCServerApp {

    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);
        RPCServer server = new NettyRPCServer(port);
        server.onMessage((header, message) -> {

            System.out.println("Header :" + header);
            MessageFormat messageFormat = Arrays.stream(MessageFormat.values()).filter(format -> format.ordinal() == header.format).findAny().orElse(MessageFormat.String);

            switch (messageFormat) {
                case String -> System.out.println("String Message :" + new String(message));
                case Json -> System.out.println("Json Message :" + new Gson().fromJson(new String(message), Map.class));
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
}
