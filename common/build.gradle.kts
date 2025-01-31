plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    api(platform(libs.spring.bom))
    api(libs.guava)

    api(platform(libs.grpc.bom))
    api(libs.grpc.api)
}
