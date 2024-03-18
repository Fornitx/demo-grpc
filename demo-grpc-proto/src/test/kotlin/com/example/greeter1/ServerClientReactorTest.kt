package com.example.greeter1

import com.example.util.*
import foo.bar.Greeter
import foo.bar.ReactorGreeter1Grpc
import io.grpc.ClientInterceptors
import io.grpc.Grpc
import io.grpc.ServerInterceptors
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import reactor.core.publisher.Mono

private val PORT = findFreePort()

class ServerClientReactorTest {
    @Test
    fun test() {
        val serverService = mock<ServerService>()
        val server = Grpc.newServerBuilderForPort(PORT, serverCredentials(false))
            .addService(ServerInterceptors.intercept(ServerReactorImpl(serverService), HeaderServerInterceptor()))
            .build()
            .start()
        println("Server started on port: $PORT")

        val clientService = mock<ClientService>()
        val channel = Grpc.newChannelBuilder("localhost:$PORT", clientCredentials(false)).build()
        val stub = ReactorGreeter1Grpc.newReactorStub(ClientInterceptors.intercept(channel, HeaderClientInterceptor()))
        stub.sayHello(Greeter.HelloRequest.newBuilder().setMsg(MSG1).build())
            .doOnNext { reply ->
                println("newReactorStub.sayHello $reply".trim() + " thread: ${Thread.currentThread()}")
                clientService.call(reply.msg)
            }
            .subscribe()

        verify(serverService, timeout(5000)).call(MSG1)

        verify(clientService, timeout(5000)).call(MSG1.repeat(3))

        verifyNoMoreInteractions(serverService, clientService)

        server.shutdown()
        server.awaitTermination()
    }
}

private class ServerReactorImpl(private val service: ServerService) : ReactorGreeter1Grpc.Greeter1ImplBase() {
    override fun sayHello(request: Greeter.HelloRequest): Mono<Greeter.HelloReply> {
        return Mono.fromRunnable<Greeter.HelloReply> {
            println("ServerReactorImpl.sayHello $request".trim() + " thread: ${Thread.currentThread()}")
            service.call(request.msg)
        }.then(Mono.defer {
            val reply = Greeter.HelloReply.newBuilder().setMsg(request.msg.repeat(3)).build()
            Mono.just(reply)
        })
    }

    override fun sayHello(request: Mono<Greeter.HelloRequest>): Mono<Greeter.HelloReply> {
        return request.flatMap { sayHello(it) }
    }
}
