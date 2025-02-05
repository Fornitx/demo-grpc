package com.example;

import com.example.foo.bar.Greeter1Grpc;
import com.example.foo.bar.HelloReply;
import com.example.foo.bar.HelloRequest;
import com.example.interceptors.Headers;
import com.example.services.ClientService;
import com.example.services.ServerService;
import com.example.utils.GrpcUtils;
import com.example.utils.ThreadUtils;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static com.example.utils.LoggingUtils.log;
import static com.example.utils.TestUtils.MSG1;
import static com.example.utils.TestUtils.MSG2;
import static com.example.utils.TestUtils.MSG3;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ServerClientJavaTest {
    @Test
    void test() throws Exception {
        var serverService = mock(ServerService.class);
        var clientService1 = mock(ClientService.class);
        var clientService2 = mock(ClientService.class);
        var clientService3 = mock(ClientService.class);
        var countDownLatch = new CountDownLatch(3);

        try (var ignored = GrpcUtils.startServer(new ServerImpl(serverService))) {
            ThreadUtils.startThread(() -> {
                try (var channel = GrpcUtils.createChannel()) {
                    var stub = GrpcUtils.createStub(channel.channel(), Greeter1Grpc::newBlockingStub);
                    var reply = stub.sayHello(HelloRequest.newBuilder().setMsg(MSG1).build());
                    log("newBlockingStub.sayHello %s", reply);
                    clientService1.call(reply.getMsg());
                    countDownLatch.countDown();
                }
            });

            ThreadUtils.startThread(() -> {
                try (var channel = GrpcUtils.createChannel()) {
                    var stub = GrpcUtils.createStub(channel.channel(), Greeter1Grpc::newStub);
                    stub.sayHello(HelloRequest.newBuilder().setMsg(MSG2).build(),
                        new StreamObserver<>() {
                            @Override
                            public void onNext(HelloReply reply) {
                                log("newStub.sayHello %s", reply);
                                clientService2.call(reply.getMsg());
                                countDownLatch.countDown();
                            }

                            @Override
                            public void onError(Throwable throwable) {
                            }

                            @Override
                            public void onCompleted() {
                            }
                        });
                }
            });

            ThreadUtils.startThread(() -> {
                try (var channel = GrpcUtils.createChannel()) {
                    var stub = GrpcUtils.createStub(channel.channel(), Greeter1Grpc::newFutureStub);
                    HelloReply reply;
                    try {
                        reply = stub.sayHello(HelloRequest.newBuilder().setMsg(MSG3).build()).get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    log("newFutureStub.sayHello %s", reply);
                    clientService3.call(reply.getMsg());
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
        }

        verify(serverService).call(MSG1);
        verify(serverService).call(MSG2);
        verify(serverService).call(MSG3);

        verify(clientService1).call(MSG1.repeat(3));
        verify(clientService2).call(MSG2.repeat(3));
        verify(clientService3).call(MSG3.repeat(3));

        verifyNoMoreInteractions(serverService, clientService1, clientService2, clientService3);
    }

    private static class ServerImpl extends Greeter1Grpc.Greeter1ImplBase {
        private final ServerService service;

        public ServerImpl(ServerService service) {
            this.service = service;
        }

        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            var requestId = Headers.REQUEST_ID_CTX_KEY.get();
            log("[%s] ServerImpl.sayHello %s", requestId, request);
            service.call(request.getMsg());
            var reply = HelloReply.newBuilder().setMsg(request.getMsg().repeat(3)).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
