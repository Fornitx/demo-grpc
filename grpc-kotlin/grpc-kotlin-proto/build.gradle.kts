plugins {
    `java-library`
    alias(libs.plugins.protobuf)
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    api("com.google.guava:guava")

    api("com.google.protobuf:protobuf-java")
    api("com.google.protobuf:protobuf-kotlin")

    api("io.grpc:grpc-api")
    api("io.grpc:grpc-stub")
    api("io.grpc:grpc-protobuf")
    api(libs.grpc.kotlin.stub)
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc" }
    plugins {
        create("grpc") { artifact = "io.grpc:protoc-gen-grpc-java" }
        create("grpckt") { artifact = "${libs.grpc.protoc.gen.kotlin.get()}:jdk8@jar" }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}
