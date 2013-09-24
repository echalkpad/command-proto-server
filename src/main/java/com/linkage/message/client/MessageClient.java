package com.linkage.message.client;

import com.linkage.message.codec.CommandBuilder;
import com.linkage.message.protoc.Commands;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * User: roger
 * Date: 13-9-18 下午4:18
 */
public class MessageClient {
    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("frame-decoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0,2))
                                    .addLast("pb-decoder", new ProtobufDecoder(Commands.Command.getDefaultInstance(), CommandBuilder.registry))
                                    .addLast("frame-encoder", new LengthFieldPrepender(2))
                                    .addLast("pb-encoder", new ProtobufEncoder())
                                    .addLast("handler", new MessageClientHandler());
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect("127.0.0.1", 8000).sync();


            f.channel().writeAndFlush(CommandBuilder.buildTokenLogin("test-token"));

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }
}
