package com.example.greeter1

import com.example.util.ClientService
import com.example.util.ServerService
import com.example.util.credentials
import com.example.util.findFreePort
import foo.bar.Greeter.HelloReply
import foo.bar.Greeter.HelloRequest
import foo.bar.Greeter1Grpc
import io.grpc.Grpc
import io.grpc.InsecureServerCredentials
import io.grpc.stub.StreamObserver
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

private val PORT = findFreePort()

class ServerClientJavaTest {
    @Test
    fun test() {
        val serverService = mock<ServerService>()
        val server = Grpc.newServerBuilderForPort(PORT, InsecureServerCredentials.create())
            .addService(ServerImpl(serverService))
            .build()
            .start()
        println("Server started on port: $PORT")

        val clientService1 = mock<ClientService>()
        Thread.startVirtualThread {
            val channel = Grpc.newChannelBuilder("localhost:$PORT", credentials(false)).build()
            val stub = Greeter1Grpc.newBlockingStub(channel)
            val reply = stub.sayHello(HelloRequest.newBuilder().setMsg("Abc").build())
            println("newBlockingStub.sayHello $reply".trim() + " thread: ${Thread.currentThread()}")
            clientService1.call(reply.msg)
        }

        val clientService2 = mock<ClientService>()
        Thread.startVirtualThread {
            val channel = Grpc.newChannelBuilder("localhost:$PORT", credentials(false)).build()
            val stub = Greeter1Grpc.newStub(channel)
            stub.sayHello(HelloRequest.newBuilder().setMsg("Xyz").build(),
                object : StreamObserver<HelloReply> {
                    override fun onNext(reply: HelloReply) {
                        println("newStub.sayHello $reply".trim() + " thread: ${Thread.currentThread()}")
                        clientService2.call(reply.msg)
                    }

                    override fun onError(th: Throwable) = Unit
                    override fun onCompleted() = Unit
                })
        }

        val clientService3 = mock<ClientService>()
        Thread.startVirtualThread {
            val channel = Grpc.newChannelBuilder("localhost:$PORT", credentials(false)).build()
            val stub = Greeter1Grpc.newFutureStub(channel)
            val reply = stub.sayHello(HelloRequest.newBuilder().setMsg("Klm").build()).get()
            println("newFutureStub.sayHello $reply".trim() + " thread: ${Thread.currentThread()}")
            clientService3.call(reply.msg)
        }

        verify(serverService, timeout(5000)).call("Abc")
        verify(serverService, timeout(5000)).call("Xyz")
        verify(serverService, timeout(5000)).call("Klm")

        verify(clientService1, timeout(5000)).call("Abc".repeat(3))
        verify(clientService2, timeout(5000)).call("Xyz".repeat(3))
        verify(clientService3, timeout(5000)).call("Klm".repeat(3))

        verifyNoMoreInteractions(serverService, clientService1, clientService2, clientService3)

        server.shutdown()
        server.awaitTermination()
    }
}

private class ServerImpl(private val service: ServerService) : Greeter1Grpc.Greeter1ImplBase() {
    override fun sayHello(
        request: HelloRequest,
        responseObserver: StreamObserver<HelloReply>
    ) {
        println("ServerImpl.sayHello $request".trim() + " thread: ${Thread.currentThread()}")
        service.call(request.msg)
        val reply = HelloReply.newBuilder().setMsg(request.msg.repeat(3)).build()
        responseObserver.onNext(reply)
        responseObserver.onCompleted()
    }
}


