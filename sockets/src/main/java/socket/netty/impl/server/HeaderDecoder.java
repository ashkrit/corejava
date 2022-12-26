package socket.netty.impl.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import socket.netty.impl.MessagePacket;

import java.util.List;

public class HeaderDecoder extends ByteToMessageDecoder {
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
