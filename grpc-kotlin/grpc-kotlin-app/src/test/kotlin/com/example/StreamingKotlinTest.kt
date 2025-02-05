package com.example

import com.example.foo.bar.Greeter2GrpcKt
import com.example.foo.bar.HelloReply
import com.example.foo.bar.HelloRequest
import com.example.foo.bar.helloReply
import com.example.foo.bar.helloRequest
import com.example.interceptors.Headers
import com.example.services.ClientService
import com.example.services.ServerService
import com.example.utils.GrpcUtils
import com.example.utils.LoggingUtils.log
import com.example.utils.TestUtils.MSG1
import com.example.utils.TestUtils.MSG2
import com.example.utils.TestUtils.MSG3
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock

class StreamingKotlinTest {
    @Test
    fun test() = runTest {
        val serverService = mock<ServerService>()
        val clientService = mock<ClientService>()

        GrpcUtils.startServer(ServerCoroutineImpl2(serverService)).use { server ->
            GrpcUtils.createChannel().use { channel ->
                val stub = GrpcUtils.createStub(channel.channel(), Greeter2GrpcKt::Greeter2CoroutineStub)
                stub.sayHello(flow {
                    emit(helloRequest { msg = MSG1 })
                    emit(helloRequest { msg = MSG2 })
                    emit(helloRequest { msg = MSG3 })
                }).collect { reply ->
                    log("ClientCoroutineStub.sayHello %s", reply)
                    clientService.call(reply.msg)
                }
            }
        }

        val serverInOrder = inOrder(serverService)
        serverInOrder.verify(serverService).call(MSG1)
        serverInOrder.verify(serverService).call(MSG2)
        serverInOrder.verify(serverService).call(MSG3)
        serverInOrder.verifyNoMoreInteractions()

        val clientInOrder = inOrder(clientService)
        clientInOrder.verify(clientService).call(MSG1.repeat(3))
        clientInOrder.verify(clientService).call(MSG2.repeat(3))
        clientInOrder.verify(clientService).call(MSG3.repeat(3))
        clientInOrder.verifyNoMoreInteractions()
    }
}

private class ServerCoroutineImpl2(
    private val serverService: ServerService
) : Greeter2GrpcKt.Greeter2CoroutineImplBase() {
    override fun sayHello(requests: Flow<HelloRequest>): Flow<HelloReply> = flow {
        requests.collect { request ->
            val requestId = Headers.REQUEST_ID_CTX_KEY.get()
            log("[%s] ServerCoroutineImpl2.sayHello %s", requestId, request)
            serverService.call(request.msg)
            emit(helloReply { msg = request.msg.repeat(3) })
        }
    }
}
