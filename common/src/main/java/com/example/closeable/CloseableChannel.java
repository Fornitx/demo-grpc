package com.example.closeable;

import io.grpc.ManagedChannel;

import java.util.concurrent.TimeUnit;

public record CloseableChannel(ManagedChannel channel) implements AutoCloseable {
    @Override
    public void close() throws Exception {
        channel.shutdown();
        channel.awaitTermination(10, TimeUnit.SECONDS);
    }
}
