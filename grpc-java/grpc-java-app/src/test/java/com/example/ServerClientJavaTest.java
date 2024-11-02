package com.example;

import com.example.foo.bar.Greeter1Grpc;
import com.example.foo.bar.HelloReply;
import com.example.foo.bar.HelloRequest;
import com.example.interceptors.HeaderClientInterceptor;
import com.example.interceptors.HeaderServerInterceptor;
import com.example.interceptors.Headers;
import com.example.services.ClientService;
import com.example.services.ServerService;
import io.grpc.ChannelCredentials;
import io.grpc.ClientInterceptors;
import io.grpc.Grpc;
import io.grpc.ServerInterceptors;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static com.example.utils.LoggingUtils.log;
import static com.example.utils.NetUtils.findFreePort;
import static com.example.utils.TestUtils.MSG1;
import static com.example.utils.TestUtils.MSG2;
import static com.example.utils.TestUtils.MSG3;
import static com.example.utils.TlsUtils.clientCredentials;
import static com.example.utils.TlsUtils.serverCredentials;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class ServerClientJavaTest {
    private static final int PORT = findFreePort();

    @Test
    void test() throws Exception {
        var serverService = mock(ServerService.class);
        var server = Grpc.newServerBuilderForPort(PORT, serverCredentials(false))
            .addService(ServerInterceptors.intercept(new ServerImpl(serverService), new HeaderServerInterceptor()))
            .build()
            .start();
        System.out.println("Server started on port: " + PORT);

        var clientService1 = mock(ClientService.class);
        Thread.startVirtualThread(() -> {
            var channel = Grpc.newChannelBuilder("localhost:" + PORT, silentCredentials(false)).build();
            var stub = Greeter1Grpc.newBlockingStub(ClientInterceptors.intercept(channel,
                new HeaderClientInterceptor()));
            var reply = stub.sayHello(HelloRequest.newBuilder().setMsg(MSG1).build());
            log("newBlockingStub.sayHello %s", reply);
            clientService1.call(reply.getMsg());
        });

        var clientService2 = mock(ClientService.class);
        Thread.startVirtualThread(() -> {
            var channel = Grpc.newChannelBuilder("localhost:" + PORT, silentCredentials(false)).build();
            var stub = Greeter1Grpc.newStub(ClientInterceptors.intercept(channel, new HeaderClientInterceptor()));
            stub.sayHello(HelloRequest.newBuilder().setMsg(MSG2).build(),
                new StreamObserver<>() {
                    @Override
                    public void onNext(HelloReply reply) {
                        log("newStub.sayHello %s", reply);
                        clientService2.call(reply.getMsg());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onCompleted() {
                    }
                });
        });

        var clientService3 = mock(ClientService.class);
        Thread.startVirtualThread(() -> {
            var channel = Grpc.newChannelBuilder("localhost:" + PORT, silentCredentials(false)).build();
            var stub = Greeter1Grpc.newFutureStub(ClientInterceptors.intercept(channel, new HeaderClientInterceptor()));
            HelloReply reply;
            try {
                reply = stub.sayHello(HelloRequest.newBuilder().setMsg(MSG3).build()).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            log("newFutureStub.sayHello %s", reply);
            clientService3.call(reply.getMsg());
        });

        verify(serverService, timeout(5000)).call(MSG1);
        verify(serverService, timeout(5000)).call(MSG2);
        verify(serverService, timeout(5000)).call(MSG3);

        verify(clientService1, timeout(5000)).call(MSG1.repeat(3));
        verify(clientService2, timeout(5000)).call(MSG2.repeat(3));
        verify(clientService3, timeout(5000)).call(MSG3.repeat(3));

        verifyNoMoreInteractions(serverService, clientService1, clientService2, clientService3);

        server.shutdown();
        server.awaitTermination();
    }

    private static ChannelCredentials silentCredentials(boolean secure) {
        try {
            return clientCredentials(secure);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
