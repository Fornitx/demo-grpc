package com.example.interceptors;

import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

import java.util.UUID;

public class HeaderServerInterceptor implements ServerInterceptor {
    private static final Key<String> KEY = Key.of("response_id", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call,
        Metadata requestHeaders,
        ServerCallHandler<ReqT, RespT> next
    ) {
        System.out.printf("[%s] headers received from client: %s%n", Thread.currentThread(), requestHeaders);
        return next.startCall(new SimpleForwardingServerCall<>(call) {
            @Override
            public void sendHeaders(Metadata responseHeaders) {
                responseHeaders.put(KEY, UUID.randomUUID().toString());
                super.sendHeaders(responseHeaders);
            }
        }, requestHeaders);
    }
}
