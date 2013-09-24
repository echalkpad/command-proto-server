package com.linkage.message.client;

import com.linkage.message.codec.CommandBuilder;
import com.linkage.message.protoc.Commands;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: roger
 * Date: 13-9-18 下午6:30
 */
public class MessageClientHandler extends SimpleChannelInboundHandler<Commands.Command> {
    private final static Logger logger = LoggerFactory.getLogger(MessageClientHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Commands.Command command) throws Exception {
        logger.info("receive protobuf message {}", command);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("client channel active {} ", ctx.channel());

        ctx.channel().writeAndFlush(CommandBuilder.buildHeartbeat());
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.info("channel {} read complete", ctx.channel());
        ctx.flush();

    }
}
