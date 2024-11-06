package com.example

import com.example.foo.bar.Greeter2GrpcKt
import com.example.foo.bar.HelloReply
import com.example.foo.bar.HelloRequest
import com.example.foo.bar.helloReply
import com.example.foo.bar.helloRequest
import com.example.interceptors.HeaderClientInterceptor
import com.example.interceptors.HeaderServerInterceptor
import com.example.interceptors.Headers
import com.example.services.ClientService
import com.example.services.ServerService
import com.example.utils.LoggingUtils.log
import com.example.utils.NetUtils.findFreePort
import com.example.utils.TestUtils.MSG1
import com.example.utils.TestUtils.MSG2
import com.example.utils.TestUtils.MSG3
import com.example.utils.TlsUtils.clientCredentials
import com.example.utils.TlsUtils.serverCredentials
import io.grpc.ClientInterceptors
import io.grpc.Grpc
import io.grpc.ServerInterceptors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.timeout

private val PORT = findFreePort()

class StreamingKotlinTest {
    @Test
    fun test() = runTest {
        val serverService = mock<ServerService>()
        val server = Grpc.newServerBuilderForPort(PORT, serverCredentials(false))
            .addService(ServerInterceptors.intercept(ServerCoroutineImpl2(serverService), HeaderServerInterceptor()))
            .build()
            .start()
        println("Server started on port: $PORT")

        val clientService = mock<ClientService>()
        val channel = Grpc.newChannelBuilder("localhost:$PORT", clientCredentials(false)).build()
        val stub =
            Greeter2GrpcKt.Greeter2CoroutineStub(ClientInterceptors.intercept(channel, HeaderClientInterceptor()))

        launch(Dispatchers.IO) {
            stub.sayHello(flow {
                emit(helloRequest { msg = MSG1 })
                emit(helloRequest { msg = MSG2 })
                emit(helloRequest { msg = MSG3 })
            }).collect { reply ->
                log("ClientCoroutineStub.sayHello %s", reply)
                clientService.call(reply.msg)
            }
        }

        val serverInOrder = inOrder(serverService)
        serverInOrder.verify(serverService, timeout(5000)).call(MSG1)
        serverInOrder.verify(serverService, timeout(5000)).call(MSG2)
        serverInOrder.verify(serverService, timeout(5000)).call(MSG3)
        serverInOrder.verifyNoMoreInteractions()

        val clientInOrder = inOrder(clientService)
        clientInOrder.verify(clientService, timeout(5000)).call(MSG1.repeat(3))
        clientInOrder.verify(clientService, timeout(5000)).call(MSG2.repeat(3))
        clientInOrder.verify(clientService, timeout(5000)).call(MSG3.repeat(3))
        clientInOrder.verifyNoMoreInteractions()

        server.shutdown()
        server.awaitTermination()
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
