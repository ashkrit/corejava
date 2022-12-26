package socket.netty.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import socket.netty.RPCClient;
import socket.netty.MessageFormat;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static socket.netty.impl.MessageHandler.createHeader;

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
                            ch.pipeline().addLast(new ClientHandler(message, (byte) format.ordinal(), consumer));
                        }
                    });

            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onReply(Consumer<byte[]> consumer) {
        this.consumer = consumer;
    }

    public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

        private final byte[] message;
        private final byte messageType;
        private final Consumer<byte[]> consumer;

        public ClientHandler(byte[] message, byte messageType, Consumer<byte[]> consumer) {
            this.message = message;
            this.messageType = messageType;
            this.consumer = consumer;
        }

        @Override
        public void channelActive(ChannelHandlerContext context) {


            ByteBuf messageBuffer = Unpooled.copiedBuffer(message);
            ByteBuf headerBuffer = createHeader(0, (byte) 0, messageType, messageBuffer.readableBytes());

            int frameSize = MessageHandler.calculateFrameSize(headerBuffer, messageBuffer);

            context.write(Unpooled.copyInt(frameSize));
            context.write(headerBuffer);
            context.write(messageBuffer);
            context.flush();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf in) {
            byte[] rawBytes = new byte[in.readableBytes()];
            in.readBytes(rawBytes);
            consumer.accept(rawBytes);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
            cause.printStackTrace();
            context.close();
        }
    }


}
