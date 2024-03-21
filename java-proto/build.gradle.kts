import com.google.protobuf.gradle.id

plugins {
    `java-library`
    id("com.google.protobuf")
}

val protobufVersion = "3.25.1"
val grpcVersion = "1.60.0"
val reactorGrpcVersion = "1.2.4"
//val grpcKotlinVersion = "1.4.1"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:" + System.getProperty("spring_version")))

    api("com.google.protobuf:protobuf-javalite:$protobufVersion")

    api("io.grpc:grpc-inprocess:$grpcVersion")
    api("io.grpc:grpc-netty-shaded:$grpcVersion")
    api("io.grpc:grpc-protobuf:$grpcVersion")
    api("io.grpc:grpc-services:$grpcVersion")
    api("io.grpc:grpc-stub:$grpcVersion")

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
