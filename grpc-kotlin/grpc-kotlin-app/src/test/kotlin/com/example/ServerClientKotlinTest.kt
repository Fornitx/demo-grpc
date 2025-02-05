package com.example

import com.example.foo.bar.Greeter1GrpcKt
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
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class ServerClientKotlinTest {
    @Test
    fun test() = runTest {
        val serverService = mock<ServerService>()
        val clientService = mock<ClientService>()

        GrpcUtils.startServer(ServerCoroutineImpl1(serverService)).use { server ->
            GrpcUtils.createChannel().use { channel ->
                val stub = GrpcUtils.createStub(channel.channel(), Greeter1GrpcKt::Greeter1CoroutineStub)
                val reply = stub.sayHello(helloRequest { msg = MSG1 })
                log("ClientCoroutineStub.sayHello %s", reply)
                clientService.call(reply.msg)
            }
        }

        verify(serverService).call(MSG1)

        verify(clientService).call(MSG1.repeat(3))

        verifyNoMoreInteractions(serverService, clientService)
    }
}

private class ServerCoroutineImpl1(private val service: ServerService) : Greeter1GrpcKt.Greeter1CoroutineImplBase() {
    override suspend fun sayHello(request: HelloRequest): HelloReply {
        val requestId = Headers.REQUEST_ID_CTX_KEY.get()
        log("[%s] ServerCoroutineImpl1.sayHello %s", requestId, request)
        service.call(request.msg)
        return helloReply { msg = request.msg.repeat(3) }
    }
}
