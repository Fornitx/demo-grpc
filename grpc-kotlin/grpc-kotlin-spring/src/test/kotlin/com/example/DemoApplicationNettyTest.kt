package com.example

import com.example.foo.bar.Greeter1GrpcKt
import com.example.foo.bar.Greeter2GrpcKt
import com.example.foo.bar.helloRequest
import com.example.utils.TestUtils.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.grpc.test.autoconfigure.LocalGrpcPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.grpc.client.GrpcChannelFactory
import org.springframework.grpc.client.NettyGrpcChannelFactory

@SpringBootTest(properties = [
    "spring.grpc.client.inprocess.enabled=false",
])
class DemoApplicationNettyTest {
    @TestConfiguration
    class TestListener {
        @Bean
        @Lazy
        fun stub1(channels: GrpcChannelFactory, @LocalGrpcPort port : Int): Greeter1GrpcKt.Greeter1CoroutineStub {
            if (channels is NettyGrpcChannelFactory) {
                return Greeter1GrpcKt.Greeter1CoroutineStub(channels.createChannel("0.0.0.0:$port"))
            } else throw IllegalArgumentException("invalid GrpcChannelFactory type ${channels::class.java}")
        }

        @Bean
        @Lazy
        fun stub2(channels: GrpcChannelFactory, @LocalGrpcPort port : Int): Greeter2GrpcKt.Greeter2CoroutineStub {
            if (channels is NettyGrpcChannelFactory) {
                return Greeter2GrpcKt.Greeter2CoroutineStub(channels.createChannel("0.0.0.0:$port"))
            } else throw IllegalArgumentException("invalid GrpcChannelFactory type ${channels::class.java}")
        }
    }

    @Autowired
    private lateinit var stub1: Greeter1GrpcKt.Greeter1CoroutineStub

    @Autowired
    private lateinit var stub2: Greeter2GrpcKt.Greeter2CoroutineStub

    @Test
    fun test1() = runTest {
        val reply = stub1.sayHello(helloRequest { msg = MSG1 })
        println("test1 reply: $reply")
    }

    @Test
    fun test2() = runTest {
        stub2.sayHello(flow {
            emit(helloRequest { msg = MSG1 })
            emit(helloRequest { msg = MSG2 })
            emit(helloRequest { msg = MSG3 })
        }).collect { reply ->
            println("test2 reply: $reply")
        }
    }
}
