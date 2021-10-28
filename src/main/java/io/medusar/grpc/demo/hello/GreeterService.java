package io.medusar.grpc.demo.hello;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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
        super.receiveSteam(request, responseObserver);
    }

    @Override
    public StreamObserver<HelloRequest> sendAndReceive(StreamObserver<HelloReply> responseObserver) {
        return super.sendAndReceive(responseObserver);
    }
}
