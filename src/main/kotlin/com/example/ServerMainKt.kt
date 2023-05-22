package com.example

import foo.bar.GreeterGrpcKt
import foo.bar.GreeterOuterClass.HelloReply
import foo.bar.GreeterOuterClass.HelloRequest
import io.grpc.Grpc
import io.grpc.InsecureServerCredentials

fun main() {
    Grpc.newServerBuilderForPort(8080, InsecureServerCredentials.create())
        .addService(GreeterCoroutineImpl())
        .build()
        .start()
        .awaitTermination()
}

private class GreeterCoroutineImpl : GreeterGrpcKt.GreeterCoroutineImplBase() {
    override suspend fun sayHello(request: HelloRequest): HelloReply {
        println("GreeterCoroutineImpl.sayHello $request")
        return HelloReply.newBuilder().setMessage(request.name.repeat(3)).build()
    }
}
