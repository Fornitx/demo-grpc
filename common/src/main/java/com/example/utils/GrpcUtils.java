package com.example.utils;

import com.example.closeable.CloseableChannel;
import com.example.closeable.CloseableServer;
import com.example.interceptors.HeaderClientInterceptor;
import com.example.interceptors.HeaderServerInterceptor;
import io.grpc.BindableService;
import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;

import java.io.IOException;
import java.util.function.Function;

public class GrpcUtils {
    private static final String IN_PROCESS_NAME = "test";

    private GrpcUtils() {
    }

    public static CloseableServer startServer(BindableService service) throws IOException {
        var server = InProcessServerBuilder.forName(IN_PROCESS_NAME)
            .directExecutor()
            .addService(interceptService(service))
            .build()
            .start();
        return new CloseableServer(server);
    }

    public static CloseableChannel createChannel() {
        var channel = InProcessChannelBuilder.forName(IN_PROCESS_NAME).directExecutor().build();
        return new CloseableChannel(channel);
    }

    public static <T> T createStub(Channel channel, Function<Channel, T> stubCreator) {
        return stubCreator.apply(interceptClient(channel));
    }

    private static ServerServiceDefinition interceptService(BindableService service) {
        return ServerInterceptors.intercept(service, new HeaderServerInterceptor());
    }

    private static Channel interceptClient(Channel channel) {
        return ClientInterceptors.intercept(channel, new HeaderClientInterceptor());
    }
}
