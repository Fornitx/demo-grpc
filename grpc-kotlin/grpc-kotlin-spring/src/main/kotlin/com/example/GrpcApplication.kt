package com.example

import com.example.foo.bar.Greeter1GrpcKt
import com.example.foo.bar.Greeter2GrpcKt
import com.example.foo.bar.HelloReply
import com.example.foo.bar.HelloRequest
import kotlinx.coroutines.flow.Flow
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GrpcApplication

fun main(args: Array<String>) {
    runApplication<GrpcApplication>(*args)
}

@GrpcService
class Greeter1Server : Greeter1GrpcKt.Greeter1CoroutineImplBase() {
    override suspend fun sayHello(request: HelloRequest): HelloReply {
        return super.sayHello(request)
    }
}

@GrpcService
class Greeter2Server : Greeter2GrpcKt.Greeter2CoroutineImplBase() {
    override fun sayHello(requests: Flow<HelloRequest>): Flow<HelloReply> {
        return super.sayHello(requests)
    }
}
