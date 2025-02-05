package com.example.closeable;

import io.grpc.Server;

import java.util.concurrent.TimeUnit;

public record CloseableServer(Server server) implements AutoCloseable {
    @Override
    public void close() throws Exception {
        server.shutdown();
        server.awaitTermination(10, TimeUnit.SECONDS);
    }
}
