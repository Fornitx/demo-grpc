package com.example.util

import io.grpc.ForwardingServerCall.SimpleForwardingServerCall
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import java.util.*

class HeaderServerInterceptor : ServerInterceptor {
    private val key = Metadata.Key.of("response_id", Metadata.ASCII_STRING_MARSHALLER)

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>?,
        requestHeaders: Metadata?,
        next: ServerCallHandler<ReqT, RespT>?
    ): ServerCall.Listener<ReqT> {
        println("[${Thread.currentThread()}] header received from client: $requestHeaders")
        return next!!.startCall(object : SimpleForwardingServerCall<ReqT, RespT>(call) {
            override fun sendHeaders(responseHeaders: Metadata) {
                responseHeaders.put(key, UUID.randomUUID().toString())
                super.sendHeaders(responseHeaders)
            }
        }, requestHeaders)
    }
}
