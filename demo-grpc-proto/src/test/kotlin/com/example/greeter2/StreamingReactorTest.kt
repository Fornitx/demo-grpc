package com.example.greeter2

import com.example.util.*
import foo.bar.Greeter
import foo.bar.ReactorGreeter2Grpc
import io.grpc.ClientInterceptors
import io.grpc.Grpc
import io.grpc.ServerInterceptors
import org.junit.jupiter.api.Test
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.timeout
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private val PORT = findFreePort()

class StreamingReactorTest {
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
        val stub = ReactorGreeter2Grpc.newReactorStub(ClientInterceptors.intercept(channel, HeaderClientInterceptor()))
        stub.sayHello(
            Flux.just(MSG1, MSG2, MSG3)
                .map { Greeter.HelloRequest.newBuilder().setMsg(it).build() }
        ).doOnNext { reply ->
            println("newReactorStub.sayHello $reply".trim() + " thread: ${Thread.currentThread()}")
            clientService.call(reply.msg)
        }.subscribe()

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

private class ServerReactorImpl(private val service: ServerService) : ReactorGreeter2Grpc.Greeter2ImplBase() {
    override fun sayHello(request: Flux<Greeter.HelloRequest>): Flux<Greeter.HelloReply> {
        return request.flatMap {
            println("ServerReactorImpl.onNext $it".trim() + " thread: ${Thread.currentThread()}")
            service.call(it.msg)
            val reply = Greeter.HelloReply.newBuilder().setMsg(it.msg.repeat(3)).build()
            Mono.just(reply)
        }
    }
}
