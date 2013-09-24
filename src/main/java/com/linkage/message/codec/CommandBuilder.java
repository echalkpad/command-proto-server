package com.linkage.message.codec;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.linkage.message.protoc.Commands;
import static com.linkage.message.protoc.Commands.*;

/**
 * User: roger
 * Date: 13-9-18 下午4:19
 */
public class CommandBuilder {
    public static final ExtensionRegistry registry= ExtensionRegistry.newInstance();

    static {
        Commands.registerAllExtensions(registry);
    }

    static <Type> Command wrap(Command.CommandType type, GeneratedMessage.GeneratedExtension<Command, Type> extension, Type cmd) {
        return Command.newBuilder().setType(type).setExtension(extension, cmd).build();
    }

    public static Command buildLoginResponse(int value, String reason) {
        return wrap(Command.CommandType.LOGIN_RESP, LoginResponse.cmd, LoginResponse.newBuilder().setCode(value).setReason(reason).build());
    }

    public static Command buildPlainLogin(String username, String password) {
        return wrap(Command.CommandType.LOGIN_REQ, LoginRequest.cmd, LoginRequest.newBuilder().setUsername(username).setPassword(password).build());
    }

    public static Command buildTokenLogin(String token) {
        return wrap(Command.CommandType.LOGIN_REQ, LoginRequest.cmd, LoginRequest.newBuilder().setToken(token).build());
    }

    public static Command buildHeartbeat() {
        return Command.newBuilder().setType(Command.CommandType.HEARTBEAT).build();
    }


}
