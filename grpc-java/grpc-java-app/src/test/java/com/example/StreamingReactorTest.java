package com.example;

import com.example.foo.bar.HelloReply;
import com.example.foo.bar.HelloRequest;
import com.example.foo.bar.ReactorGreeter2Grpc;
import com.example.interceptors.HeaderClientInterceptor;
import com.example.interceptors.HeaderServerInterceptor;
import com.example.interceptors.Headers;
import com.example.services.ClientService;
import com.example.services.ServerService;
import io.grpc.ClientInterceptors;
import io.grpc.Grpc;
import io.grpc.ServerInterceptors;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.example.utils.LoggingUtils.log;
import static com.example.utils.NetUtils.findFreePort;
import static com.example.utils.TestUtils.MSG1;
import static com.example.utils.TestUtils.MSG2;
import static com.example.utils.TestUtils.MSG3;
import static com.example.utils.TlsUtils.clientCredentials;
import static com.example.utils.TlsUtils.serverCredentials;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;

class StreamingReactorTest {
    private static final int PORT = findFreePort();

    @Test
    void test() throws Exception {
        var serverService = mock(ServerService.class);
        var server = Grpc.newServerBuilderForPort(PORT, serverCredentials(false))
            .addService(ServerInterceptors.intercept(new ServerReactorImpl(serverService), new HeaderServerInterceptor()))
            .build()
            .start();
        System.out.println("Server started on port: " + PORT);

        var clientService = mock(ClientService.class);
        var channel = Grpc.newChannelBuilder("localhost:" + PORT, clientCredentials(false)).build();
        var stub = ReactorGreeter2Grpc.newReactorStub(ClientInterceptors.intercept(channel, new HeaderClientInterceptor()));
        Flux.just(MSG1, MSG2, MSG3)
            .map(msg -> HelloRequest.newBuilder().setMsg(msg).build())
            .as(stub::sayHello)
            .doOnNext(reply -> {
                log("newReactorStub.sayHello %s", reply);
                clientService.call(reply.getMsg());
            })
            .subscribe();

        var serverInOrder = inOrder(serverService);
        serverInOrder.verify(serverService, timeout(5000)).call(MSG1);
        serverInOrder.verify(serverService, timeout(5000)).call(MSG2);
        serverInOrder.verify(serverService, timeout(5000)).call(MSG3);
        serverInOrder.verifyNoMoreInteractions();

        var clientInOrder = inOrder(clientService);
        clientInOrder.verify(clientService, timeout(5000)).call(MSG1.repeat(3));
        clientInOrder.verify(clientService, timeout(5000)).call(MSG2.repeat(3));
        clientInOrder.verify(clientService, timeout(5000)).call(MSG3.repeat(3));
        clientInOrder.verifyNoMoreInteractions();

        server.shutdown();
        server.awaitTermination();
    }

    private class ServerReactorImpl extends ReactorGreeter2Grpc.Greeter2ImplBase {
        private final ServerService service;

        public ServerReactorImpl(ServerService service) {
            this.service = service;
        }

        @Override
        public Flux<HelloReply> sayHello(Flux<HelloRequest> requests) {
            return requests.flatMap(request -> {
                var requestId = Headers.REQUEST_ID_CTX_KEY.get();
                log("[%s] ServerReactorImpl.onNext %s", requestId, request);
                service.call(request.getMsg());
                var reply = HelloReply.newBuilder().setMsg(request.getMsg().repeat(3)).build();
                return Mono.just(reply);
            });
        }
    }
}
