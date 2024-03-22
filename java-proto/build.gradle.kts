import com.google.protobuf.gradle.id

plugins {
    `java-library`
    id("com.google.protobuf")
}

val protobufVersion: String by project
val grpcVersion: String by project
val reactorGrpcVersion: String by project
val guavaVersion: String by project

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:" + System.getProperty("spring_version")))

    api("com.google.protobuf:protobuf-javalite:$protobufVersion")
    api("com.google.protobuf:protobuf-java:$protobufVersion")
    api("com.google.protobuf:protobuf-java-util:$protobufVersion")

    api("io.grpc:grpc-inprocess:$grpcVersion")
    api("io.grpc:grpc-netty-shaded:$grpcVersion")
    api("io.grpc:grpc-protobuf:$grpcVersion")
    api("io.grpc:grpc-services:$grpcVersion")
    api("io.grpc:grpc-stub:$grpcVersion")

    api("com.google.guava:guava:$guavaVersion")

    api("io.projectreactor:reactor-core")
    api("com.salesforce.servicelibs:reactor-grpc-stub:$reactorGrpcVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:$protobufVersion" }
    plugins {
        id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion" }
        id("reactor") { artifact = "com.salesforce.servicelibs:reactor-grpc:$reactorGrpcVersion" }
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
