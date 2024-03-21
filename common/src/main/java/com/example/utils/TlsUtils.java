package com.example.utils;

import io.grpc.ChannelCredentials;
import io.grpc.InsecureChannelCredentials;
import io.grpc.InsecureServerCredentials;
import io.grpc.ServerCredentials;
import io.grpc.TlsChannelCredentials;
import io.grpc.TlsServerCredentials;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;

public class TlsUtils {
    private static final char[] password = "password".toCharArray();

    private TlsUtils() {
    }

    public static ServerCredentials serverCredentials(boolean secure) throws Exception {
        if (secure) {
            return TlsServerCredentials.newBuilder()
                .keyManager(keyManagerFactory().getKeyManagers())
                .trustManager(trustManagerFactory().getTrustManagers())
                .build();
        }
        return InsecureServerCredentials.create();
    }

    public static ChannelCredentials clientCredentials(boolean secure) throws Exception {
        if (secure) {
            return TlsChannelCredentials.newBuilder()
                .keyManager(keyManagerFactory().getKeyManagers())
                .trustManager(trustManagerFactory().getTrustManagers())
                .build();
        }
        return InsecureChannelCredentials.create();
    }

    /**
     * private fun keyManagerFactory(): KeyManagerFactory {
     * return KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
     * init(KeyStore.getInstance("PKCS12").apply {
     * File("keystore.p12").inputStream().use {
     * load(it, password)
     * }
     * }, password)
     * }
     * }
     */
    private static KeyManagerFactory keyManagerFactory() throws Exception {
        var keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        var keyStore = KeyStore.getInstance("PKCS12");
        try (var inputStream = Files.newInputStream(Paths.get("keystore.p12"))) {
            keyStore.load(inputStream, password);
        }
        keyManagerFactory.init(keyStore, password);
        return keyManagerFactory;
    }

    /**
     * private fun trustManagerFactory(): TrustManagerFactory {
     * return TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
     * init(KeyStore.getInstance("PKCS12").apply {
     * File("truststore.p12").inputStream().use {
     * load(it, password)
     * }
     * })
     * }
     * }
     */
    private static TrustManagerFactory trustManagerFactory() throws Exception {
        var trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        var keyStore = KeyStore.getInstance("PKCS12");
        try (var inputStream = Files.newInputStream(Paths.get("truststore.p12"))) {
            keyStore.load(inputStream, password);
        }
        trustManagerFactory.init(keyStore);
        return trustManagerFactory;
    }
}
