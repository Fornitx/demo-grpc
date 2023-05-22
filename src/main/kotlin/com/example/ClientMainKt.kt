package com.example

import foo.bar.GreeterGrpcKt
import foo.bar.helloRequest
import io.grpc.Grpc
import io.grpc.InsecureChannelCredentials
import kotlinx.coroutines.runBlocking

fun main() {
    val channel = Grpc.newChannelBuilder("localhost:8080", InsecureChannelCredentials.create()).build()
    val stub = GreeterGrpcKt.GreeterCoroutineStub(channel)
    runBlocking {
        val reply = stub.sayHello(helloRequest { name = "Abc" })
        println("ClientMainKt.sayHello $reply")
    }
}
