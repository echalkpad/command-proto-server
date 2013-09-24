package com.linkage.message;

import akka.actor.ActorSystem;
import com.linkage.message.codec.MessageServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.linkage.message.protoc.Commands.*;
import static com.linkage.message.codec.CommandBuilder.*;

/**
 * User: roger
 * Date: 13-9-12 下午3:30
 */
public class MessageServer {
    static final Logger logger = LoggerFactory.getLogger(MessageServer.class);

    public static void main(String[] args) throws Exception {



        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {


                            ch.pipeline().addLast("frame-decoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
                                    .addLast("pb-decoder", new ProtobufDecoder(Command.getDefaultInstance(), registry))
                                    .addLast("frame-encoder", new LengthFieldPrepender(2))
                                    .addLast("pb-encoder", new ProtobufEncoder())
                                    .addLast("handler", new MessageServerHandler());

                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(8000).sync();

            logger.info("Server listen on port 8000");
            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
