package com.example

import com.example.foo.bar.*
import com.example.utils.LoggingUtils.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GrpcConfig {
    @Bean
    fun greeter1Service(): Greeter1GrpcKt.Greeter1CoroutineImplBase =
        object : Greeter1GrpcKt.Greeter1CoroutineImplBase() {
            override suspend fun sayHello(request: HelloRequest): HelloReply {
                log("greeter1Service - %s", request)
                return helloReply { msg = request.msg.repeat(3) }
            }
        }

    @Bean
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
