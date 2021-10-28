package io.medusar;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.medusar.grpc.demo.hello.GreeterService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class GrpcServer {

    private final int port;
    private final Server server;

    public GrpcServer(int port) throws IOException {
        this(ServerBuilder.forPort(port), port);
    }

    public GrpcServer(ServerBuilder<?> serverBuilder, int port) {
        this.port = port;
        this.server = serverBuilder
                .addService(new GreeterService()) //添加提供的服务
                .addService(ProtoReflectionService.newInstance()) //反射服务，方便查看服务信息或测试调用
                .build();
    }

    public void start() throws IOException {
        server.start();
        log.info("Server started, listening on " + port);
    }

    public void startAndWait() throws IOException, InterruptedException {
        start();
        server.awaitTermination();
    }


    public void stop() {
        server.shutdown();
    }
}
