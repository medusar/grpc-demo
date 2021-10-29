package io.medusar.interceptor;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHeaderInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> CUSTOM_HEADER_KEY =
            Metadata.Key.of("custom_server_header_key", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
                                                                 ServerCallHandler<ReqT, RespT> next) {

        //headers: Metadata(content-type=application/grpc,user-agent=grpcurl/v1.8.5 grpc-go/1.37.0),
        //call attributes:{remote-addr=/127.0.0.1:60310, io.grpc.internal.GrpcAttributes.securityLevel=NONE, local-addr=/127.0.0.1:6666}
        log.info("interceptCall, headers:{}, call attributes:{}", headers, call.getAttributes());

        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void sendHeaders(Metadata responseHeaders) {

                //自定义response header
                headers.put(CUSTOM_HEADER_KEY, "customRespondValue");

                super.sendHeaders(responseHeaders);
            }

        }, headers);
    }
}
