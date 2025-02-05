package com.example;

import com.example.foo.bar.HelloReply;
import com.example.foo.bar.HelloRequest;
import com.example.foo.bar.ReactorGreeter2Grpc;
import com.example.interceptors.Headers;
import com.example.services.ClientService;
import com.example.services.ServerService;
import com.example.utils.GrpcUtils;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.example.utils.LoggingUtils.log;
import static com.example.utils.TestUtils.MSG1;
import static com.example.utils.TestUtils.MSG2;
import static com.example.utils.TestUtils.MSG3;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class StreamingReactorTest {
    @Test
    void test() throws Exception {
        var serverService = mock(ServerService.class);
        var clientService = mock(ClientService.class);

        try (var ignored = GrpcUtils.startServer(new ServerReactorImpl(serverService))) {
            try (var channel = GrpcUtils.createChannel()) {
                var stub = GrpcUtils.createStub(channel.channel(), ReactorGreeter2Grpc::newReactorStub);
                Flux.just(MSG1, MSG2, MSG3)
                    .map(msg -> HelloRequest.newBuilder().setMsg(msg).build())
                    .as(stub::sayHello)
                    .doOnNext(reply -> {
                        log("newReactorStub.sayHello %s", reply);
                        clientService.call(reply.getMsg());
                    })
                    .blockLast();
            }
        }

        var serverInOrder = inOrder(serverService);
        serverInOrder.verify(serverService).call(MSG1);
        serverInOrder.verify(serverService).call(MSG2);
        serverInOrder.verify(serverService).call(MSG3);
        serverInOrder.verifyNoMoreInteractions();

        var clientInOrder = inOrder(clientService);
        clientInOrder.verify(clientService).call(MSG1.repeat(3));
        clientInOrder.verify(clientService).call(MSG2.repeat(3));
        clientInOrder.verify(clientService).call(MSG3.repeat(3));
        clientInOrder.verifyNoMoreInteractions();
    }

    private static class ServerReactorImpl extends ReactorGreeter2Grpc.Greeter2ImplBase {
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
