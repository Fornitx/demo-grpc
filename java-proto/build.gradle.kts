import com.google.protobuf.gradle.id

plugins {
    id("buildlogic.java-common-conventions")
    `java-library`
    alias(libs.plugins.protobuf.gradle.plugin)
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:" + System.getProperty("spring.version")))

    api(libs.protobuf.java)

    api(libs.grpc.protobuf)
    api(libs.grpc.stub)

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
