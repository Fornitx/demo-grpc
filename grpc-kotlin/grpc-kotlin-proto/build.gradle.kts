import com.google.protobuf.gradle.id

plugins {
    `java-library`
    alias(libs.plugins.protobuf)
}

dependencies {
    api(platform(libs.protobuf.bom))
    api(libs.protobuf.java)
    api(libs.protobuf.kotlin)

    api(platform(libs.grpc.bom))
    api(libs.grpc.api)
    api(libs.grpc.stub)
    api(libs.grpc.kotlin.stub)
    api(libs.grpc.protobuf)
    api(libs.guava)

    api(platform(libs.kotlinx.coroutines.bom))
    api(libs.kotlinx.coroutines.core)
}

protobuf {
    protoc { artifact = libs.protobuf.protoc.get().toString() }
    plugins {
        id("grpc") { artifact = libs.grpc.protoc.gen.java.get().toString() }
        id("grpckt") { artifact = "${libs.grpc.protoc.gen.kotlin.get()}:jdk8@jar" }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}
