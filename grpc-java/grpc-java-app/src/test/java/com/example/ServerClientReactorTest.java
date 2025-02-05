package com.example;

import com.example.foo.bar.HelloReply;
import com.example.foo.bar.HelloRequest;
import com.example.foo.bar.ReactorGreeter1Grpc;
import com.example.interceptors.Headers;
import com.example.services.ClientService;
import com.example.services.ServerService;
import com.example.utils.GrpcUtils;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static com.example.utils.LoggingUtils.log;
import static com.example.utils.TestUtils.MSG1;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ServerClientReactorTest {
    @Test
    void test() throws Exception {
        var serverService = mock(ServerService.class);
        var clientService = mock(ClientService.class);

        try (var ignored = GrpcUtils.startServer(new ServerReactorImpl(serverService))) {
            try (var channel = GrpcUtils.createChannel()) {
                var stub = GrpcUtils.createStub(channel.channel(), ReactorGreeter1Grpc::newReactorStub);
                stub.sayHello(HelloRequest.newBuilder().setMsg(MSG1).build())
                    .doOnNext(reply -> {
                        log("newReactorStub.sayHello %s", reply);
                        clientService.call(reply.getMsg());
                    }).block();
            }
        }

        verify(serverService).call(MSG1);

        verify(clientService).call(MSG1.repeat(3));

        verifyNoMoreInteractions(serverService, clientService);
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
