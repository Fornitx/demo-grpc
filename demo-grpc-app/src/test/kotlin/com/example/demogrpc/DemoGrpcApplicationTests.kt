package com.example.demogrpc

import foo.bar.Greeter1GrpcKt
import foo.bar.Greeter2GrpcKt
import foo.bar.helloRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import net.devh.boot.grpc.client.inject.GrpcClient
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

private val log = KotlinLogging.logger {}

@SpringBootTest
class DemoGrpcApplicationTests {
    @GrpcClient("client1")
    private lateinit var client1: Greeter1GrpcKt.Greeter1CoroutineStub

    @GrpcClient("client2")
    private lateinit var client2: Greeter2GrpcKt.Greeter2CoroutineStub

    @Test
    fun test() = runTest {
        log.info { "client1 = $client1" }
        log.info { "client2 = $client2" }

        val reply1 = client1.sayHello(helloRequest { msg = "123" })
        log.info { "reply1 = $reply1" }

        val flow2 = client2.sayHello(flowOf(helloRequest { msg = "abc" }))
        flow2.collect { reply2 ->
            log.info { "reply2 = $reply2" }
        }
    }
}
