package com.linkage.message.codec;

import com.google.common.base.CaseFormat;
import com.google.protobuf.GeneratedMessage;
import com.linkage.message.protoc.Commands;
import static com.linkage.message.protoc.Commands.*;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * User: roger
 * Date: 13-9-17 下午3:27
 */
public class MessageServerHandler extends SimpleChannelInboundHandler<Commands.Command> {
    private final static Logger logger = LoggerFactory.getLogger(MessageServerHandler.class);

    private Channel channel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Commands.Command decoded) throws Exception {
        logger.info("receive protobuf decoded message {}", decoded);
        this.channel = ctx.channel();
        invokeHandle(decoded);
    }

    private void invokeHandle(Commands.Command decoded) throws Exception {
        Command.CommandType com = decoded.getType();
        String methodName= "handle"+ CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, com.name());

        for(Method method : getClass().getDeclaredMethods()) {
            if(method.getName().equalsIgnoreCase(methodName)) {
                method.setAccessible(true);
                Class[] types = method.getParameterTypes();
                Class cmdType = types[0];
                Field f = cmdType.getField("cmd");
                GeneratedMessage.GeneratedExtension ext = (GeneratedMessage.GeneratedExtension)f.get(decoded);

                method.invoke(this, decoded.getExtension(ext));
                break;
            }
        }
    }

    private void handleLoginReq(LoginRequest login) {
        logger.info("channel {} invoke handleLoginReq : {}", channel, login);
        channel.writeAndFlush(CommandBuilder.buildLoginResponse(0,"success"));
    }

    private void handleHeartbeat(Heartbeat hb) {
        logger.info("channel {} invoke heartbeat {} ", channel, hb);


    }

}
