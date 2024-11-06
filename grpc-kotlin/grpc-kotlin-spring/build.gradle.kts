plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dm)
}

ext["kotlin-coroutines.version"] = libs.versions.kotlin.coroutines.get()
ext["junit-jupiter.version"] = libs.versions.junit.jupiter.get()

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    implementation(rootProject.libs.kotlin.reflect)
    implementation(rootProject.libs.kotlinx.coroutines.core)

    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")

    implementation(project(":common"))
    implementation(project(":grpc-kotlin:grpc-kotlin-proto"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation(rootProject.libs.kotlin.test.junit5)
    testImplementation(rootProject.libs.kotlinx.coroutines.test)
}

tasks.jar {
    enabled = false
}
