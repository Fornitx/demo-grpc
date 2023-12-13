package com.example.demogrpc.grpc

import foo.bar.Greeter
import foo.bar.Greeter1GrpcKt
import foo.bar.helloReply
import io.github.oshai.kotlinlogging.KotlinLogging
import net.devh.boot.grpc.server.service.GrpcService

private val log = KotlinLogging.logger {}

@GrpcService
class DemoGrpcServer : Greeter1GrpcKt.Greeter1CoroutineImplBase() {
    override suspend fun sayHello(request: Greeter.HelloRequest): Greeter.HelloReply {
        log.info { ">>> $request" }
        return helloReply { msg = request.msg.repeat(3) }
    }
}
