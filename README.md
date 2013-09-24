### Protocol buffer polymorphism java demo

在netty中使用protobuf的时候，由于netty的ProtobufDecoder中只能传递一种MessageLite的instance,导致netty server在初始化pipeline后
只能解析一种protobuf的message格式，比如：
```
new ServerBootstrap()..childHandler(new ChannelInitializer<SocketChannel>() {
     ch.pipeline().addLast("decoder", new ProtobufDecoder(MyMessage.getDefaultInstance());
});

```

解决的办法有

### Union Type
直接使用protobuf的union type来解决
```
message Heartbeat {
   
}
message Login {
   optional string username;
   optional string password;
}

message Message {
   optional Heartbeat heartbeat;
   optional Login login;
   ....

}

```

