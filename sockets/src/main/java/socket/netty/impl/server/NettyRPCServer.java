package socket.netty.impl.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import socket.netty.PanicCodeExecutor;
import socket.netty.impl.MessageHandler.MessageHeader;
import socket.netty.RPCServer;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import static socket.netty.PanicCodeExecutor.execute;

public class NettyRPCServer implements RPCServer {

    private final int port;
    private NioEventLoopGroup group;
    private ChannelFuture channelFuture;
    private final ExecutorService es = Executors.newSingleThreadExecutor();
    private BiConsumer<MessageHeader, byte[]> consumer = (header, message) -> {
        System.out.println("Header :" + header);
        System.out.println("Message :" + new String(message));
    };

    public NettyRPCServer(int port) {
        this.port = port;
    }

    public void start() {

        es.submit(() -> _start());

    }

    private void _start() {
        this.group = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        InetSocketAddress localAddress = new InetSocketAddress(port);
        b.group(group).channel(NioServerSocketChannel.class).localAddress(localAddress).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new HeaderDecoder(), new ServerMessageHandler(consumer));

            }
        });

        this.channelFuture = b.bind();
        System.out.println("Server Started at " + localAddress);
    }

    @Override
    public void stop() {

        System.out.println("Stopping server now");
        execute(() -> channelFuture.channel().disconnect().sync());
        es.shutdownNow();
        System.out.println("Done");

    }

    @Override
    public void onMessage(BiConsumer<MessageHeader, byte[]> consumer) {
        this.consumer = consumer;
    }


}
