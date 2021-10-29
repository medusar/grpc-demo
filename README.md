## 服务启动

App.java run

## 启动后利用grpcurl查看服务信息

注意，使用grpcurl的前提是，server端启动的时候添加reflection service:

```
serverBuilder
            .addService(new GreeterService()) //添加提供的服务
            .addService(ProtoReflectionService.newInstance()) //反射服务，方便查看服务信息或测试调用
            .build();
```

1. 查看server提供的服务信息
   ```
   $ grpcurl -plaintext 127.0.0.1:6666 list                                                                                                                                                                                                                           
    grpc.reflection.v1alpha.ServerReflection
    hello.Greeter
   ```
2. 查看某个service的方法
   ```
   $ grpcurl -plaintext 127.0.0.1:6666 list hello.Greeter                                                                                                                                                                                                            
    hello.Greeter.SayHello
   ```
3. 通过describe查看服务详情
   ```
    $ grpcurl -plaintext 127.0.0.1:6666 describe hello.Greeter                                                                                                                                                                                                      
    hello.Greeter is a service:
    service Greeter {
    rpc SayHello ( .hello.HelloRequest ) returns ( .hello.HelloReply );
    }
   ```
4. 通过describe查看某个方法
   ```
    $ grpcurl -plaintext 127.0.0.1:6666 describe hello.Greeter.SayHello                                                                                                                                                                                                 
    hello.Greeter.SayHello is a method:
    rpc SayHello ( .hello.HelloRequest ) returns ( .hello.HelloReply );
   ```
5. 通过describe查看参数或返回值
   ```
    $ grpcurl -plaintext 127.0.0.1:6666 describe hello.HelloRequest                                                                                                                                                                                                    
    hello.HelloRequest is a message:
    message HelloRequest {
        string name = 1;
    }
   
   $ grpcurl -plaintext 127.0.0.1:6666 describe hello.HelloReply                                                                                                                                                                                                       
    hello.HelloReply is a message:
    message HelloReply {
        string message = 1;
    }
   ```
6. 调用某个方法   
   接口与方法名之间使用`/`分隔
   ```
    $ grpcurl -plaintext -d '{"name":"medusar"}' 127.0.0.1:6666 hello.Greeter/SayHello                                                                                                                                                                                
    {
        "message": "hello medusar"
    }
   ```
   Client端stream:
   ```
       grpcurl -plaintext -d @  127.0.0.1:6666 hello.Greeter/SendAndReceive <<EOF                                                                                                                                                                                
         {"name":"jason"}
         {"name":"jason"}
         {"name":"jason"}
         {"name":"jason"}
         {"name":"jason"}
         EOF
         {
         "message": "hi jason"
         }
         {
         "message": "hi jason"
         }
         {
         "message": "hi jason"
         }
         {
         "message": "hi jason"
         }
         {
         "message": "hi jason"
         }
   ```