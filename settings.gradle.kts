pluginManagement {
    plugins {
        id("org.springframework.boot") version System.getProperty("spring.version")
        id("io.spring.dependency-management") version System.getProperty("spring.dm.version")
        kotlin("jvm") version System.getProperty("kotlin.version")
        kotlin("plugin.spring") version System.getProperty("kotlin.version")

        id("com.google.protobuf") version "0.9.4"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "demo-grpc"

include("common", "java-app", "java-proto", "kotlin-app", "kotlin-proto")
