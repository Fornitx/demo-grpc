package com.example.interceptors;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

import static com.example.interceptors.Headers.REQUEST_ID_KEY;
import static com.example.utils.LoggingUtils.log;

public class HeaderServerInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call,
        Metadata requestHeaders,
        ServerCallHandler<ReqT, RespT> next
    ) {
        log("headers received from client: %s%n", requestHeaders);

        var requestId = requestHeaders.get(REQUEST_ID_KEY);
        if (requestId == null) {
            return next.startCall(call, requestHeaders);
        }

        var context = Context.current().withValue(Headers.REQUEST_ID_CTX_KEY, requestId);
        return Contexts.interceptCall(context, new SimpleForwardingServerCall<>(call) {
            @Override
            public void sendHeaders(Metadata responseHeaders) {
                responseHeaders.put(REQUEST_ID_KEY, requestId);
                super.sendHeaders(responseHeaders);
            }
        }, requestHeaders, next);
    }
}
