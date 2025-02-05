package com.example;

import com.example.foo.bar.Greeter2Grpc;
import com.example.foo.bar.HelloReply;
import com.example.foo.bar.HelloRequest;
import com.example.interceptors.Headers;
import com.example.services.ClientService;
import com.example.services.ServerService;
import com.example.utils.GrpcUtils;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;

import static com.example.utils.LoggingUtils.log;
import static com.example.utils.TestUtils.MSG1;
import static com.example.utils.TestUtils.MSG2;
import static com.example.utils.TestUtils.MSG3;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class StreamingJavaTest {
    @Test
    void test() throws Exception {
        var serverService = mock(ServerService.class);
        var clientService = mock(ClientService.class);

        try (var ignored = GrpcUtils.startServer(new ServerImpl(serverService))) {
            try (var channel = GrpcUtils.createChannel()) {
                var stub = GrpcUtils.createStub(channel.channel(), Greeter2Grpc::newStub);
                var requestObserver = stub.sayHello(
                    new StreamObserver<>() {
                        @Override
                        public void onNext(HelloReply reply) {
                            log("newStub.sayHello %s", reply);
                            clientService.call(reply.getMsg());
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            log("newStub.onError %s", throwable.getMessage());
                        }

                        @Override
                        public void onCompleted() {
                            log("newStub.onCompleted");
                        }
                    }
                );

                requestObserver.onNext(HelloRequest.newBuilder().setMsg(MSG1).build());
                requestObserver.onNext(HelloRequest.newBuilder().setMsg(MSG2).build());
                requestObserver.onNext(HelloRequest.newBuilder().setMsg(MSG3).build());
                requestObserver.onCompleted();
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

    private static class ServerImpl extends Greeter2Grpc.Greeter2ImplBase {
        private final ServerService service;

        public ServerImpl(ServerService service) {
            this.service = service;
        }

        @Override
        public StreamObserver<HelloRequest> sayHello(StreamObserver<HelloReply> responseObserver) {
            return new StreamObserver<>() {
                @Override
                public void onNext(HelloRequest request) {
                    String requestId = Headers.REQUEST_ID_CTX_KEY.get();
                    log("[%s] ServerImpl.onNext %s", requestId, request);
                    service.call(request.getMsg());
                    var reply = HelloReply.newBuilder().setMsg(request.getMsg().repeat(3)).build();
                    responseObserver.onNext(reply);
                }

                @Override
                public void onError(Throwable throwable) {
                    log("ServerImpl.onError %s%n", throwable.getMessage());
                    responseObserver.onError(throwable);
                }

                @Override
                public void onCompleted() {
                    log("ServerImpl.onCompleted");
                    responseObserver.onCompleted();
                }
            };
        }
    }
}
