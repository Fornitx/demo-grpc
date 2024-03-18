package com.example.util

import io.grpc.*
import java.io.File
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory

private val password = "password".toCharArray()

fun serverCredentials(secure: Boolean): ServerCredentials = if (secure) {
    TlsServerCredentials.newBuilder()
        .keyManager(*keyManagerFactory().keyManagers)
        .trustManager(*trustManagerFactory().trustManagers)
        .build()
} else {
    InsecureServerCredentials.create()
}

fun clientCredentials(secure: Boolean): ChannelCredentials = if (secure) {
    TlsChannelCredentials.newBuilder()
        .keyManager(*keyManagerFactory().keyManagers)
        .trustManager(*trustManagerFactory().trustManagers)
        .build()
} else {
    InsecureChannelCredentials.create()
}

private fun keyManagerFactory(): KeyManagerFactory {
    return KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
        init(KeyStore.getInstance("PKCS12").apply {
            File("keystore.p12").inputStream().use {
                load(it, password)
            }
        }, password)
    }
}

private fun trustManagerFactory(): TrustManagerFactory {
    return TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
        init(KeyStore.getInstance("PKCS12").apply {
            File("truststore.p12").inputStream().use {
                load(it, password)
            }
        })
    }
}
