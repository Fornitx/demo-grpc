package com.example

import com.example.interceptors.HeaderClientInterceptor
import com.example.interceptors.HeaderServerInterceptor
import com.example.services.ClientService
import com.example.services.ServerService
import com.example.utils.NetUtils.findFreePort
import com.example.utils.TestUtils.*
import com.example.utils.TlsUtils.clientCredentials
import com.example.utils.TlsUtils.serverCredentials
import foo.bar.Greeter.HelloReply
import foo.bar.Greeter.HelloRequest
import foo.bar.Greeter2GrpcKt
import foo.bar.helloReply
import foo.bar.helloRequest
import io.grpc.ClientInterceptors
import io.grpc.Grpc
import io.grpc.ServerInterceptors
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.timeout

private val PORT = findFreePort()

class StreamingKotlinTest {
    @Test
    fun test() {
        val serverService = mock<ServerService>()
        val server = Grpc.newServerBuilderForPort(PORT, serverCredentials(false))
            .addService(ServerInterceptors.intercept(ServerCoroutineImpl(serverService), HeaderServerInterceptor()))
            .build()
            .start()
        println("Server started on port: $PORT")

        val clientService = mock<ClientService>()
        val channel = Grpc.newChannelBuilder("localhost:$PORT", clientCredentials(false)).build()
        val stub = Greeter2GrpcKt.Greeter2CoroutineStub(ClientInterceptors.intercept(channel, HeaderClientInterceptor()))
        GlobalScope.launch {
            stub.sayHello(flow {
                emit(helloRequest { msg = MSG1 })
                emit(helloRequest { msg = MSG2 })
                emit(helloRequest { msg = MSG3 })
            }).collect { reply ->
                print("[${Thread.currentThread()}] ClientCoroutineStub.sayHello $reply")
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

private class ServerCoroutineImpl(
    private val serverService: ServerService
) : Greeter2GrpcKt.Greeter2CoroutineImplBase() {
    override fun sayHello(requests: Flow<HelloRequest>): Flow<HelloReply> {
        return flow {
            requests.collect { request ->
                print("[${Thread.currentThread()}] ServerCoroutineImpl.sayHello $request")
                serverService.call(request.msg)
                emit(helloReply { msg = request.msg.repeat(3) })
            }
        }
    }
}

