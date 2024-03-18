import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")

    id("com.google.protobuf") version "0.9.4"
}

val protobufVersion = "3.25.1"
val grpcVersion = "1.60.0"
val reactorGrpcVersion = "1.2.4"
val grpcKotlinVersion = "1.4.1"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {

    api("com.google.protobuf:protobuf-javalite:$protobufVersion")
//    api("com.google.protobuf:protobuf-java:$protobufVersion")
//    api("com.google.protobuf:protobuf-java-util:$protobufVersion")
    api("com.google.protobuf:protobuf-kotlin:$protobufVersion")

    api("io.grpc:grpc-inprocess:$grpcVersion")
    api("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    api("io.grpc:grpc-netty-shaded:$grpcVersion")
    api("io.grpc:grpc-protobuf:$grpcVersion")
    api("io.grpc:grpc-services:$grpcVersion")
    api("io.grpc:grpc-stub:$grpcVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")

//    implementation(platform("io.projectreactor:reactor-bom:2022.0.7"))
    implementation("io.projectreactor:reactor-core")
    implementation("com.salesforce.servicelibs:reactor-grpc-stub:$reactorGrpcVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("io.projectreactor:reactor-test")

    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")

    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:$protobufVersion" }
    plugins {
        id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion" }
        id("grpckt") { artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk8@jar" }
        id("reactor") { artifact = "com.salesforce.servicelibs:reactor-grpc:$reactorGrpcVersion" }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
                id("reactor")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}
