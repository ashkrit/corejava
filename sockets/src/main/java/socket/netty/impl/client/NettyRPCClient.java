package socket.netty.impl.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import socket.netty.MessageFormat;
import socket.netty.RPCClient;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static socket.netty.PanicCodeExecutor.execute;

public class NettyRPCClient implements RPCClient {

    private final String host;
    private final int port;
    private Consumer<byte[]> consumer = reply -> System.out.println("Reply :" + new String(reply));
    private ExecutorService es = Executors.newCachedThreadPool();

    public NettyRPCClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public void send(byte[] message, MessageFormat format) {
        es.submit(() -> _send(message, format));
    }

    private void _send(byte[] message, MessageFormat format) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ClientHandler(consumer, (byte) format.ordinal(), message));
                        }
                    });

            execute(() -> {
                ChannelFuture f = b.connect().sync();
                f.channel().closeFuture().sync();
            });

        } finally {
            execute(() -> group.shutdownGracefully().sync());
        }
    }

    @Override
    public void onReply(Consumer<byte[]> consumer) {
        this.consumer = consumer;
    }

}
