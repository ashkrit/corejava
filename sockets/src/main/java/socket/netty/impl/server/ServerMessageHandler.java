package socket.netty.impl.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import socket.netty.impl.MessageHandler;

import java.util.function.BiConsumer;

public class ServerMessageHandler extends ChannelInboundHandlerAdapter {

    private final BiConsumer<MessageHandler.MessageHeader, byte[]> consumer;

    public ServerMessageHandler(BiConsumer<MessageHandler.MessageHeader, byte[]> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object object) {
        ByteBuf in = (ByteBuf) object;

        MessageHandler.MessageHeader header = MessageHandler.readHeader(in);
        ByteBuf message = in.readBytes(header.messageSize);

        int readIndex = message.readerIndex();
        decodeMessage(header, message);

        message.readerIndex(readIndex);

        context.write(message); //Send same reply... This is not must
    }

    private void decodeMessage(MessageHandler.MessageHeader header, ByteBuf message) {
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
