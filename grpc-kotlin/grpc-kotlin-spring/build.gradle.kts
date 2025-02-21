plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

ext["kotlin-coroutines.version"] = libs.versions.kotlin.coroutines.get()

dependencies {
    implementation("org.springframework.grpc:spring-grpc-spring-boot-starter")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    implementation(project(":common"))
    implementation(project(":grpc-kotlin:grpc-kotlin-proto"))

    implementation("io.grpc:grpc-services")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.grpc:spring-grpc-test") {
        exclude(group = "junit", module = "junit")
    }

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}

tasks.jar {
    enabled = false
}
