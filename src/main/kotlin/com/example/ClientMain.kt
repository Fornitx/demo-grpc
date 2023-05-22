package com.example

import foo.bar.GreeterGrpc
import foo.bar.GreeterOuterClass.HelloRequest
import io.grpc.Grpc
import io.grpc.InsecureChannelCredentials

fun main() {
    val channel = Grpc.newChannelBuilder("localhost:8080", InsecureChannelCredentials.create()).build()
    val stub = GreeterGrpc.newBlockingStub(channel)
    val reply = stub.sayHello(HelloRequest.newBuilder().setName("Abc").build())
    println("ClientMain.sayHello $reply")
}
