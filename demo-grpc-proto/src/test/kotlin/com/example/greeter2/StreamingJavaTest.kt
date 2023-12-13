package com.example.greeter2

import com.example.util.ClientService
import com.example.util.ServerService
import com.example.util.credentials
import com.example.util.findFreePort
import foo.bar.Greeter.HelloReply
import foo.bar.Greeter.HelloRequest
import foo.bar.Greeter2Grpc
import io.grpc.Grpc
import io.grpc.InsecureServerCredentials
import io.grpc.stub.StreamObserver
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.timeout

private val PORT = findFreePort()

class StreamingJavaTest {
    @Test
    fun test() {
        val serverService = mock<ServerService>()
        val server = Grpc.newServerBuilderForPort(PORT, InsecureServerCredentials.create())
            .addService(ServerImpl(serverService))
            .build()
            .start()
        println("Server started on port: $PORT")

        val clientService = mock<ClientService>()
        val channel = Grpc.newChannelBuilder("localhost:$PORT", credentials(false)).build()
        val stub = Greeter2Grpc.newStub(channel)
        val requestObserver = stub.sayHello(
            object : StreamObserver<HelloReply> {
                override fun onNext(reply: HelloReply) {
                    println("newStub.sayHello $reply".trim() + " thread: ${Thread.currentThread()}")
                    clientService.call(reply.msg)
                }

                override fun onError(th: Throwable) = println("newStub.onError ${th.stackTraceToString()}")
                override fun onCompleted() = println("newStub.onCompleted")
            })

        requestObserver.onNext(HelloRequest.newBuilder().setMsg("Abc").build())
        requestObserver.onNext(HelloRequest.newBuilder().setMsg("Xyz").build())
        requestObserver.onNext(HelloRequest.newBuilder().setMsg("Klm").build())
        requestObserver.onCompleted()

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

private class ServerImpl(private val service: ServerService) : Greeter2Grpc.Greeter2ImplBase() {
    override fun sayHello(responseObserver: StreamObserver<HelloReply>): StreamObserver<HelloRequest> {
        return object : StreamObserver<HelloRequest> {
            override fun onNext(request: HelloRequest) {
                println("ServerImpl.onNext $request".trim() + " thread: ${Thread.currentThread()}")
                service.call(request.msg)
                val reply = HelloReply.newBuilder().setMsg(request.msg.repeat(3)).build()
                responseObserver.onNext(reply)
            }

            override fun onError(th: Throwable) {
                println("ServerImpl.onError ${th.stackTraceToString()}")
                responseObserver.onError(th)
            }

            override fun onCompleted() {
                println("ServerImpl.onCompleted")
                responseObserver.onCompleted()
            }
        }
    }
}


