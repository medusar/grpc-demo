package io.medusar.grpc.demo.hello;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GreeterService extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, io.grpc.stub.StreamObserver<HelloReply> responseObserver) {
        log.info("sayHello, request:{}", request.getName());
        responseObserver.onNext(HelloReply.newBuilder().setMessage("hello " + request.getName()).build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<HelloRequest> sendStream(StreamObserver<HelloReply> responseObserver) {
        log.info("sendStream invoked");

        return new StreamObserver<HelloRequest>() {

            private List<String> names = new ArrayList<>();

            @Override
            public void onNext(HelloRequest request) {
                names.add(request.getName());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("error sendStream", throwable);
//                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(HelloReply.newBuilder()
                        .setMessage("hello to " + names.size() + " users: " + names)
                        .build());
                responseObserver.onCompleted();
            }
        };

    }

    @Override
    public void receiveSteam(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        log.info("receiveSteam invoked, request:{}", request);
        for (int i = 0; i < 10; i++) {
            responseObserver.onNext(HelloReply.newBuilder().setMessage("hello" + i).build());
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<HelloRequest> sendAndReceive(StreamObserver<HelloReply> responseObserver) {
        log.info("sendAndReceive received");
        return new StreamObserver<HelloRequest>() {
            @Override
            public void onNext(HelloRequest request) {
                log.info("sendAndReceive request received:{}", request);
                //收到一个请求返回一个response
                responseObserver.onNext(HelloReply.newBuilder().setMessage("hi " + request.getName()).build());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("sendAndReceive error", throwable);
//                responseObserver.onError(throwable);  //这一行貌似没啥用？
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
