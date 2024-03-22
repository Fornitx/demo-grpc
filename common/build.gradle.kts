plugins {
    `java-library`
}

val grpcVersion: String by project
val guavaVersion: String by project

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    api("io.grpc:grpc-api:$grpcVersion")
    api("com.google.guava:guava:$guavaVersion")
}

tasks.test {
    useJUnitPlatform()
}
