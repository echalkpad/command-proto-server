### Protocol buffer polymorphism java demo

在netty中使用protobuf的时候，由于netty的ProtobufDecoder中只能传递一种MessageLite的instance,导致netty server在初始化pipeline后
只能解析一种protobuf的message格式，比如：
```
new ServerBootstrap()..childHandler(new ChannelInitializer<SocketChannel>() {
     ch.pipeline().addLast("decoder", new ProtobufDecoder(MyMessage.getDefaultInstance());
});

```

一般的解决办法是使用一个消息体里定义可选optional字段来一一对应不同的消息体内容，这种方法在protobuf里被称为Union Types
https://developers.google.com/protocol-buffers/docs/techniques?hl=zh-CN#union
```
enum CommandType {
   HEARTBEAT = 1;
   LOGIN = 2;
}
message Heartbeat {
   
}
message Login {
   optional string username = 1;
   optional string password = 2;
}

message Message {
   required CommandType type = 1;
   optional Heartbeat heartbeat = 2;
   optional Login login = 3;
   ....

}

```
服务端解析的代码
```
protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
   switch(message.getType()) {
       case HEARTBEAT : break;
       case LOGIN : break;
   }            
} 
```
