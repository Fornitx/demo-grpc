package com.example.greeter1

import com.example.util.ClientService
import com.example.util.ServerService
import com.example.util.credentials
import com.example.util.findFreePort
import foo.bar.Greeter.HelloReply
import foo.bar.Greeter.HelloRequest
import foo.bar.Greeter1GrpcKt
import foo.bar.helloReply
import foo.bar.helloRequest
import io.grpc.Grpc
import io.grpc.InsecureServerCredentials
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

private val PORT = findFreePort()

class ServerClientKotlinTest {
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
        val stub = Greeter1GrpcKt.Greeter1CoroutineStub(channel)
        runBlocking {
            val reply = stub.sayHello(helloRequest { msg = "Abc" })
            println("ClientCoroutineStub.sayHello $reply".trim() + " thread: ${Thread.currentThread()}")
            clientService.call(reply.msg)
        }

        verify(serverService, timeout(5000)).call("Abc")

        verify(clientService, timeout(5000)).call("Abc".repeat(3))

        verifyNoMoreInteractions(serverService, clientService)

        server.shutdown()
        server.awaitTermination()
    }
}

private class ServerCoroutineImpl(private val service: ServerService) : Greeter1GrpcKt.Greeter1CoroutineImplBase() {
    override suspend fun sayHello(request: HelloRequest): HelloReply {
        println("ServerCoroutineImpl.sayHello $request".trim() + " thread: ${Thread.currentThread()}")
        service.call(request.msg)
        return helloReply { msg = request.msg.repeat(3) }
    }
}
