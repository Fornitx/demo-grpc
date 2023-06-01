import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    id("com.google.protobuf") version "0.9.3"
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
}

dependencies {
    val protobufVersion = "3.23.1"
    val grpcVersion = "1.55.1"
//    val reactorGrpcVersion = "1.2.4"
    val grpcKotlinVersion = "1.3.0"
    val coroutinesVersion = "1.7.1"

    implementation("com.google.protobuf:protobuf-java:$protobufVersion")
    implementation("com.google.protobuf:protobuf-java-util:$protobufVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")

    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

//    implementation(platform("io.projectreactor:reactor-bom:2022.0.7"))
//    implementation("io.projectreactor:reactor-core")
//    implementation("com.salesforce.servicelibs:reactor-grpc-stub:$reactorGrpcVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
//    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "19"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

protobuf {
    val protobufVersion = "3.23.1"
    val grpcVersion = "1.55.1"
    val grpcKotlinVersion = "1.3.0"
//    val reactorGrpcVersion = "1.2.4"

    protoc { artifact = "com.google.protobuf:protoc:$protobufVersion" }
    plugins {
        id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion" }
        id("grpckt") { artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk8@jar" }
//        id("reactor") { artifact = "com.salesforce.servicelibs:reactor-grpc:$reactorGrpcVersion" }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
//                id("reactor")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}
