### Protocol buffer polymorphism java demo
这个项目是这篇文章http://www.indelible.org/ink/protobuf-polymorphism/
对应的java实现

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
       case HEARTBEAT : message.getHeartbeat(); break;
       case LOGIN : message.getLogin(); break;
   }            
} 
```

Union Types的问题在于当command type很多的时候，就显得过于笨拙，而且所有的消息都作为一个optional字段定义在一个大消息体内，protobuf在解析的
时候会尝试每种可能性。

google官方在针对大量type解析的时候给出的解决方案也是extensions

