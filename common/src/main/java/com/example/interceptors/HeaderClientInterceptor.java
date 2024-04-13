package com.example.interceptors;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

import java.util.UUID;

import static com.example.utils.LoggingUtils.log;

public class HeaderClientInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
        MethodDescriptor<ReqT, RespT> method,
        CallOptions callOptions,
        Channel next
    ) {
        return new SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(Headers.REQUEST_ID_KEY, UUID.randomUUID().toString());
                super.start(new SimpleForwardingClientCallListener<>(responseListener) {
                    @Override
                    public void onHeaders(Metadata responseHeaders) {
                        log("headers received from server: %s%n", responseHeaders);
                        super.onHeaders(responseHeaders);
                    }
                }, headers);
            }
        };
    }
}
