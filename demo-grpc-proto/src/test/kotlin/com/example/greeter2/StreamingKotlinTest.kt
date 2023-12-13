package com.example.greeter2

import com.example.util.ClientService
import com.example.util.ServerService
import com.example.util.credentials
import com.example.util.findFreePort
import foo.bar.Greeter.HelloReply
import foo.bar.Greeter.HelloRequest
import foo.bar.Greeter2GrpcKt
import foo.bar.helloReply
import foo.bar.helloRequest
import io.grpc.Grpc
import io.grpc.InsecureServerCredentials
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.timeout

private val PORT = findFreePort()

class StreamingKotlinTest {
    @Test
    fun test() {
        val serverService = mock<ServerService>()
        val server = Grpc.newServerBuilderForPort(PORT, InsecureServerCredentials.create())
            .addService(ServerCoroutineImpl(serverService))
            .build()
            .start()
        println("Server started on port: $PORT")

        val clientService = mock<ClientService>()
        val channel = Grpc.newChannelBuilder("localhost:$PORT", credentials(false)).build()
        val stub = Greeter2GrpcKt.Greeter2CoroutineStub(channel)
        runBlocking {
            stub.sayHello(flow {
                emit(helloRequest { msg = "Abc" })
                emit(helloRequest { msg = "Xyz" })
                emit(helloRequest { msg = "Klm" })
            }).collect { reply ->
                println("ClientCoroutineStub.sayHello $reply".trim() + " thread: ${Thread.currentThread()}")
                clientService.call(reply.msg)
            }
        }

        val serverInOrder = inOrder(serverService)
        serverInOrder.verify(serverService, timeout(5000)).call("Abc")
        serverInOrder.verify(serverService, timeout(5000)).call("Xyz")
        serverInOrder.verify(serverService, timeout(5000)).call("Klm")
        serverInOrder.verifyNoMoreInteractions()

        val clientInOrder = inOrder(clientService)
        clientInOrder.verify(clientService, timeout(5000)).call("Abc".repeat(3))
        clientInOrder.verify(clientService, timeout(5000)).call("Xyz".repeat(3))
        clientInOrder.verify(clientService, timeout(5000)).call("Klm".repeat(3))
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
                println("ServerCoroutineImpl.sayHello $request".trim() + " thread: ${Thread.currentThread()}")
                serverService.call(request.msg)
                emit(helloReply { msg = request.msg.repeat(3) })
            }
        }
    }
}
