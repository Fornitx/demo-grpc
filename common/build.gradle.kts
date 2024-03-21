plugins {
    `java-library`
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
    api("io.grpc:grpc-api:$grpcVersion")
}

tasks.test {
    useJUnitPlatform()
}
