import com.google.protobuf.gradle.id

plugins {
    `java-library`
    alias(libs.plugins.protobuf)
}

dependencies {
    api(platform(libs.protobuf.bom))
    api(libs.protobuf.java)

    api(platform(libs.grpc.bom))
    api(libs.grpc.api)
    api(libs.grpc.stub)
    api(libs.grpc.protobuf)
    api(libs.guava)

    api(platform(libs.spring.bom))
    api(libs.reactor.core)
    api(libs.grpc.reactor.stub)
}

protobuf {
    protoc { artifact = libs.protobuf.protoc.get().toString() }
    plugins {
        id("grpc") { artifact = libs.grpc.protoc.gen.java.get().toString() }
        id("reactor") { artifact = libs.grpc.reactor.plugin.get().toString() }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("reactor")
            }
        }
    }
}
