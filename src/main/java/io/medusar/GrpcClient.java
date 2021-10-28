package io.medusar;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.medusar.grpc.demo.hello.GreeterGrpc;
import io.medusar.grpc.demo.hello.HelloReply;
import io.medusar.grpc.demo.hello.HelloRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class GrpcClient {

    private ManagedChannel managedChannel;

    private GreeterGrpc.GreeterStub greeterStub;
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;
    private GreeterGrpc.GreeterFutureStub greeterFutureStub;

    public GrpcClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    public GrpcClient(ManagedChannelBuilder channelBuilder) {
        this.managedChannel = channelBuilder.build();

        greeterStub = GreeterGrpc.newStub(this.managedChannel);
        greeterBlockingStub = GreeterGrpc.newBlockingStub(this.managedChannel);
        greeterFutureStub = GreeterGrpc.newFutureStub(this.managedChannel);
    }

    public HelloReply sayHello(HelloRequest request) {
        return greeterBlockingStub.sayHello(request);
    }

    public HelloReply sendStream(Collection<HelloRequest> names) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<HelloReply> replyRef = new AtomicReference<>();

        StreamObserver<HelloReply> responseObserver = new StreamObserver<HelloReply>() {
            @Override
            public void onNext(HelloReply helloReply) {
                replyRef.set(helloReply);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("error on respseObserver", throwable);
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        };

        StreamObserver<HelloRequest> requestObserver = greeterStub.sendStream(responseObserver);
        try {
            for (HelloRequest request : names) {
                requestObserver.onNext(request);
            }
        } catch (Exception e) {
            log.error("error sendStream", e);
            requestObserver.onError(e);
            throw e;
        }
        requestObserver.onCompleted();


        boolean await = countDownLatch.await(1, TimeUnit.MINUTES);
        if (!await) {
            throw new IllegalStateException("timeout waiting for sendStream");
        }
        return replyRef.get();
    }

}
