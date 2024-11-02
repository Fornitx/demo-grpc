package com.example;

import com.example.foo.bar.HelloReply;
import com.example.foo.bar.HelloRequest;
import com.example.foo.bar.ReactorGreeter1Grpc;
import com.example.interceptors.HeaderClientInterceptor;
import com.example.interceptors.HeaderServerInterceptor;
import com.example.interceptors.Headers;
import com.example.services.ClientService;
import com.example.services.ServerService;
import io.grpc.ClientInterceptors;
import io.grpc.Grpc;
import io.grpc.ServerInterceptors;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static com.example.utils.LoggingUtils.log;
import static com.example.utils.NetUtils.findFreePort;
import static com.example.utils.TestUtils.MSG1;
import static com.example.utils.TlsUtils.clientCredentials;
import static com.example.utils.TlsUtils.serverCredentials;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ServerClientReactorTest {
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
        var stub = ReactorGreeter1Grpc.newReactorStub(ClientInterceptors.intercept(channel, new HeaderClientInterceptor()));
        stub.sayHello(HelloRequest.newBuilder().setMsg(MSG1).build())
            .doOnNext(reply -> {
                log("newReactorStub.sayHello %s", reply);
                clientService.call(reply.getMsg());
            }).subscribe();

        verify(serverService, timeout(5000)).call(MSG1);

        verify(clientService, timeout(5000)).call(MSG1.repeat(3));

        verifyNoMoreInteractions(serverService, clientService);

        server.shutdown();
        server.awaitTermination();
    }

    private static class ServerReactorImpl extends ReactorGreeter1Grpc.Greeter1ImplBase {
        private final ServerService service;

        public ServerReactorImpl(ServerService service) {
            this.service = service;
        }

        @Override
        public Mono<HelloReply> sayHello(HelloRequest request) {
            return Mono.fromRunnable(() -> {
                String requestId = Headers.REQUEST_ID_CTX_KEY.get();
                log("[%s] ServerReactorImpl.sayHello %s", requestId, request);
                service.call(request.getMsg());
            }).then(Mono.defer(() -> {
                var reply = HelloReply.newBuilder().setMsg(request.getMsg().repeat(3)).build();
                return Mono.just(reply);
            }));
        }

        @Override
        public Mono<HelloReply> sayHello(Mono<HelloRequest> request) {
            return request.flatMap(this::sayHello);
        }
    }
}
