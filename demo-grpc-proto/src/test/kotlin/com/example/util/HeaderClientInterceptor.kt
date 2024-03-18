package com.example.util

import io.grpc.*
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener
import java.util.*

class HeaderClientInterceptor : ClientInterceptor {
    private val key = Metadata.Key.of("request_id", Metadata.ASCII_STRING_MARSHALLER)

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>?,
        callOptions: CallOptions?,
        next: Channel?
    ): ClientCall<ReqT, RespT> {
        return object : SimpleForwardingClientCall<ReqT, RespT>(next!!.newCall(method, callOptions)) {
            override fun start(responseListener: Listener<RespT>?, headers: Metadata) {
                headers.put(key, UUID.randomUUID().toString())
                super.start(object : SimpleForwardingClientCallListener<RespT>(responseListener) {
                    override fun onHeaders(responseHeaders: Metadata) {
                        println("[${Thread.currentThread()}] header received from server: $responseHeaders")
                        super.onHeaders(responseHeaders)
                    }
                }, headers)
            }
        }
    }
}
