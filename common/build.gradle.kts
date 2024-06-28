plugins {
    `java-library`
}

val grpcVersion: String by project
val guavaVersion: String by project

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    api("io.grpc:grpc-api:$grpcVersion")
    api("com.google.guava:guava:$guavaVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
