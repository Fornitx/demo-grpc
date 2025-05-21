plugins {
    `java-library`
    alias(libs.plugins.protobuf)
}

dependencies {
    api("io.projectreactor:reactor-core")
    api("com.google.guava:guava")

    api(libs.grpc.reactor.stub) {
//        TODO
//        exclude(group = "javax.annotation")
    }

    api("com.google.protobuf:protobuf-java")

    api("io.grpc:grpc-api")
    api("io.grpc:grpc-stub")
    api("io.grpc:grpc-protobuf")
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc" }
    plugins {
        create("grpc") { artifact = "io.grpc:protoc-gen-grpc-java" }
        create("reactor") { artifact = libs.grpc.reactor.plugin.get().toString() }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc") {
                    option("@generated=omit")
                }
                create("reactor") {
                    option("@generated=omit")
                }
            }
        }
    }
}
