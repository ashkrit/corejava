package socket.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        String host = args[0];
        int port = Integer.parseInt(args[1]);


        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        reader
                .lines().filter(l -> !l.isEmpty())
                .forEach(line -> {
                    new EchoClient(host, port)
                            .start(line);
                });


    }

    private void start(String message) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new EchoClientHandler(message));
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

    @ChannelHandler.Sharable
    public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

        private final String message;

        public EchoClientHandler(String message) {
            this.message = message;
        }

        @Override
        public void channelActive(ChannelHandlerContext context) {
            context.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf in) {
            System.out.println("Client Message:" + in.toString(CharsetUtil.UTF_8));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
            cause.printStackTrace();
            context.close();
        }
    }

}
