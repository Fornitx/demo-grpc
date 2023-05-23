package com.example

import foo.bar.GreeterGrpc
import foo.bar.GreeterOuterClass.HelloRequest
import io.grpc.ChannelCredentials
import io.grpc.Grpc
import io.grpc.InsecureChannelCredentials
import io.grpc.TlsChannelCredentials
import java.io.File
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory

private val password = "password".toCharArray()

fun main() {
    val channel = Grpc.newChannelBuilder("localhost:8080", credentials(false)).build()
    val stub = GreeterGrpc.newBlockingStub(channel)
    val reply = stub.sayHello(HelloRequest.newBuilder().setName("Abc").build())
    println("ClientMain.sayHello $reply")
}

fun credentials(secure: Boolean): ChannelCredentials? = if (secure) {
    TlsChannelCredentials.newBuilder()
        .keyManager(*keyManagerFactory().keyManagers)
        .trustManager(*trustManagerFactory().trustManagers)
        .build()
} else {
    InsecureChannelCredentials.create()
}

fun keyManagerFactory(): KeyManagerFactory {
    val ks = KeyStore.getInstance("PKCS12")
    File("keystore.p12").inputStream().use {
        ks.load(it, password)
    }
    return KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply { init(ks, password) }
}

fun trustManagerFactory(): TrustManagerFactory {
    val ts = KeyStore.getInstance("PKCS12")
    File("truststore.p12").inputStream().use {
        ts.load(it, password)
    }
    return TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply { init(ts) }
}
