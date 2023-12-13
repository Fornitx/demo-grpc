package com.example.demogrpc.grpc

import foo.bar.Greeter
import foo.bar.Greeter2GrpcKt
import foo.bar.helloReply
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.devh.boot.grpc.server.service.GrpcService

private val log = KotlinLogging.logger {}

@GrpcService
class DemoStreamingGrpcServer : Greeter2GrpcKt.Greeter2CoroutineImplBase() {
    override fun sayHello(requests: Flow<Greeter.HelloRequest>): Flow<Greeter.HelloReply> {
        return flow {
            requests.collect { request ->
                log.info { ">>> $request" }
                emit(helloReply { msg = request.msg.repeat(3) })
            }
        }
    }
}
