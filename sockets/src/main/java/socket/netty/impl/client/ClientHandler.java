package socket.netty.impl.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import socket.netty.impl.MessageHandler;

import java.util.function.Consumer;

import static socket.netty.impl.MessageHandler.writeHeader;

public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final byte[] message;
    private final byte messageType;
    private final Consumer<byte[]> consumer;

    public ClientHandler(Consumer<byte[]> consumer, byte messageType, byte[] message) {
        this.message = message;
        this.messageType = messageType;
        this.consumer = consumer;
    }

    @Override
    public void channelActive(ChannelHandlerContext context) {


        ByteBuf messageBuffer = Unpooled.copiedBuffer(message);
        ByteBuf headerBuffer = writeHeader(0, (byte) 0, messageType, messageBuffer.readableBytes());

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
