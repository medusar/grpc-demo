package io.medusar;

import com.google.gson.internal.Streams;
import io.medusar.grpc.demo.hello.HelloReply;
import io.medusar.grpc.demo.hello.HelloRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Slf4j
public class GrpcClientTest {

    private GrpcClient grpcClient;
    private GrpcServer grpcServer;

    @Before
    public void init() throws IOException, InterruptedException {
        grpcServer = new GrpcServer(6666);
        grpcServer.start();

        grpcClient = new GrpcClient("127.0.0.1", 6666);
    }

    @Test
    public void testSayHello() {
        HelloReply reply = grpcClient.sayHello(HelloRequest.newBuilder().setName("jason").build());
        assertEquals("hello jason", reply.getMessage());
    }

    @Test
    public void testSendStream() throws InterruptedException {
        List<HelloRequest> requestList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            requestList.add(HelloRequest.newBuilder().setName("user" + i).build());
        }
        HelloReply reply = grpcClient.sendStream(requestList);
        log.info("reply:" + reply.getMessage());
    }
}