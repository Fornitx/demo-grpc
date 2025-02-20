plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dm)
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
    testImplementation("org.springframework.grpc:spring-grpc-test")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}

configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("junit:junit"))
            .using(module("io.quarkus:quarkus-junit4-mock:3.19.0"))
            .because(
                "We don't want JUnit 4; but is an unneeded transitive of testcontainers. " +
                        "See https://github.com/testcontainers/testcontainers-java/issues/970"
            )
    }
}

tasks.jar {
    enabled = false
}
