package socket.netty.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import socket.netty.impl.MessageHandler.MessageHeader;
import socket.netty.RPCServer;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class NetyRPCServer implements RPCServer {

    private final int port;
    private NioEventLoopGroup group;
    private ChannelFuture channelFuture;
    private final ExecutorService es = Executors.newSingleThreadExecutor();
    private BiConsumer<MessageHeader, byte[]> consumer = (header, message) -> {
        System.out.println("Header :" + header);
        System.out.println("Message :" + new String(message));
    };

    public NetyRPCServer(int port) {
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

        try {
            channelFuture.channel().disconnect().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        // group.shutdown();
        es.shutdownNow();
        System.out.println("Done");

    }

    @Override
    public void onMessage(BiConsumer<MessageHeader, byte[]> consumer) {
        this.consumer = consumer;
    }


    public static class HeaderDecoder extends ByteToMessageDecoder {
        int readIndex = 0;

        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) {

            readIndex = in.readerIndex();
            if (in.readableBytes() < MessagePacket.frameLengthInByte()) {
                return;
            }
            int frameSize = in.readInt();
            if (in.readableBytes() < frameSize) {
                return;
            }

            list.add(in.readBytes(frameSize));

        }
    }

    public static class ServerMessageHandler extends ChannelInboundHandlerAdapter {

        private final BiConsumer<MessageHeader, byte[]> consumer;

        public ServerMessageHandler(BiConsumer<MessageHeader, byte[]> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void channelRead(ChannelHandlerContext context, Object object) {
            ByteBuf in = (ByteBuf) object;

            MessageHeader header = MessageHandler.readHeader(in);
            ByteBuf message = in.readBytes(header.messageSize);


            int readIndex = message.readerIndex();
            decodeMessage(header, message);

            message.readerIndex(readIndex);
            context.write(message);
        }

        private void decodeMessage(MessageHeader header, ByteBuf message) {
            byte[] rawBytes = new byte[message.readableBytes()];
            message.readBytes(rawBytes);
            consumer.accept(header, rawBytes);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext context) {
            context.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
            cause.printStackTrace();
            context.close();
        }

    }


}
