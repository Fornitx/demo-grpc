package com.example.interceptors;

import io.grpc.Context;
import io.grpc.Metadata;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public class Headers {
    private Headers() {
    }

    public static final Context.Key<String> REQUEST_ID_CTX_KEY = Context.key("request-id");
    public static final Metadata.Key<String> REQUEST_ID_KEY = Metadata.Key.of("request-id", ASCII_STRING_MARSHALLER);
}
