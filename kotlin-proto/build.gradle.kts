import com.google.protobuf.gradle.id

plugins {
    id("buildlogic.kotlin-common-conventions")
    `java-library`
    alias(libs.plugins.protobuf.gradle.plugin)
}

dependencies {
    api(libs.protobuf.kotlin)

    api(libs.grpc.protobuf)
    api(libs.grpc.stub)
    api(libs.grpc.kotlin.stub)

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
