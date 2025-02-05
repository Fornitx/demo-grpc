package com.example

import com.example.foo.bar.Greeter1GrpcKt
import com.example.foo.bar.Greeter2GrpcKt
import com.example.foo.bar.HelloReply
import com.example.foo.bar.HelloRequest
import com.example.foo.bar.helloReply
import com.example.utils.LoggingUtils.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.context.annotation.Configuration
import org.springframework.grpc.server.service.GrpcService

@Configuration
class GrpcConfig {
    @GrpcService
    fun greeter1Service(): Greeter1GrpcKt.Greeter1CoroutineImplBase =
        object : Greeter1GrpcKt.Greeter1CoroutineImplBase() {
            override suspend fun sayHello(request: HelloRequest): HelloReply {
                log("greeter1Service - %s", request)
                return helloReply { msg = request.msg.repeat(3) }
            }
        }

    @GrpcService
    fun greeter2Service(): Greeter2GrpcKt.Greeter2CoroutineImplBase =
        object : Greeter2GrpcKt.Greeter2CoroutineImplBase() {
            override fun sayHello(requests: Flow<HelloRequest>): Flow<HelloReply> = flow {
                requests.collect { request ->
                    log("greeter2Service - %s", request)
                    emit(helloReply { msg = request.msg.repeat(3) })
                }
            }
        }
}