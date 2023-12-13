package com.example.util

import io.grpc.ChannelCredentials
import io.grpc.InsecureChannelCredentials
import io.grpc.TlsChannelCredentials
import java.io.File
import java.net.ServerSocket
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory

private val password = "password".toCharArray()

fun credentials(secure: Boolean): ChannelCredentials? = if (secure) {
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

fun findFreePort(): Int {
    val socket = ServerSocket(0)
    val localPort = socket.localPort
    socket.close()
    return localPort
}
