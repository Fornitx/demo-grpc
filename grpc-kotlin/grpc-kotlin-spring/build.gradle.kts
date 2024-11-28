plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dm)
}

ext["kotlin-coroutines.version"] = libs.versions.kotlin.coroutines.get()

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.grpc.spring.server)

    implementation(project(":common"))
    implementation(project(":grpc-kotlin:grpc-kotlin-proto"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.kotlinx.coroutines.test)
}

tasks.jar {
    enabled = false
}
