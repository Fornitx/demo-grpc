package com.example.interceptors;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.MethodDescriptor;

import java.util.UUID;

public class HeaderClientInterceptor implements ClientInterceptor {
    private static final Key<String> KEY = Key.of("request_id", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        Channel next
    ) {
        return new SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(KEY, UUID.randomUUID().toString());
                super.start(new SimpleForwardingClientCallListener<>(responseListener) {
                    @Override
                    public void onHeaders(Metadata responseHeaders) {
                        System.out.printf("[%s] headers received from server: %s%n",
                            Thread.currentThread(),
                            responseHeaders);
                        super.onHeaders(responseHeaders);
                    }
                }, headers);
            }
        };
    }
}
