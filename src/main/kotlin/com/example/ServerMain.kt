package com.example

import foo.bar.GreeterGrpc.GreeterImplBase
import foo.bar.GreeterOuterClass
import foo.bar.GreeterOuterClass.HelloReply
import io.grpc.Grpc
import io.grpc.InsecureServerCredentials
import io.grpc.stub.StreamObserver

fun main() {
    Grpc.newServerBuilderForPort(8080, InsecureServerCredentials.create())
        .addService(GreeterImpl())
        .build()
        .start()
        .awaitTermination()
}

private class GreeterImpl : GreeterImplBase() {
    override fun sayHello(
        request: GreeterOuterClass.HelloRequest,
        responseObserver: StreamObserver<HelloReply>
    ) {
        println("GreeterImpl.sayHello $request")
        val reply = HelloReply.newBuilder().setMessage(request.name.repeat(3)).build()
        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }
}
