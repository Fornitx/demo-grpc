package com.example

import com.example.foo.bar.Greeter1GrpcKt
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
import com.example.utils.TlsUtils.clientCredentials
import com.example.utils.TlsUtils.serverCredentials
import io.grpc.ClientInterceptors
import io.grpc.Grpc
import io.grpc.ServerInterceptors
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        val server = Grpc.newServerBuilderForPort(PORT, serverCredentials(false))
            .addService(ServerInterceptors.intercept(ServerCoroutineImpl1(serverService), HeaderServerInterceptor()))
            .build()
            .start()
        println("Server started on port: $PORT")

        val clientService = mock<ClientService>()
        val channel = Grpc.newChannelBuilder("localhost:$PORT", clientCredentials(false)).build()
        val stub = Greeter1GrpcKt.Greeter1CoroutineStub(ClientInterceptors.intercept(channel, HeaderClientInterceptor()))
        GlobalScope.launch {
            val reply = stub.sayHello(helloRequest { msg = MSG1 })
            log("ClientCoroutineStub.sayHello %s", reply)
            clientService.call(reply.msg)
        }

        verify(serverService, timeout(5000)).call(MSG1)

        verify(clientService, timeout(5000)).call(MSG1.repeat(3))

        verifyNoMoreInteractions(serverService, clientService)

        server.shutdown()
        server.awaitTermination()
    }
}

private class ServerCoroutineImpl1(private val service: ServerService) : Greeter1GrpcKt.Greeter1CoroutineImplBase() {
    override suspend fun sayHello(request: HelloRequest): HelloReply {
        val requestId = Headers.REQUEST_ID_CTX_KEY.get()
        log("[%s] ServerCoroutineImpl.sayHello %s", requestId, request)
        service.call(request.msg)
        return helloReply { msg = request.msg.repeat(3) }
    }
}
