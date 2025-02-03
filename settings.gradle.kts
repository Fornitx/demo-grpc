dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("kotlin-lang", providers.gradleProperty("kotlin-lang.version").get())
            version("kotlin-coroutines", providers.gradleProperty("kotlin-coroutines.version").get())
            version("kotlin-logging", providers.gradleProperty("kotlin-logging.version").get())
            version("spring-boot", providers.gradleProperty("spring-boot.version").get())
            version("spring-dm", providers.gradleProperty("spring-dm.version").get())
        }
    }
}

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("org.kordamp.gradle:enforcer-gradle-plugin:0.14.0")
    }
}

apply(plugin = "org.kordamp.gradle.enforcer")

configure<org.kordamp.gradle.plugin.enforcer.api.BuildEnforcerExtension> {
//    rule(enforcer.rules.DependencyConvergence::class.java)
}

rootProject.name = "demo-grpc"

include("common")
include("grpc-java", "grpc-java:grpc-java-proto", "grpc-java:grpc-java-app")
include("grpc-kotlin", "grpc-kotlin:grpc-kotlin-proto", "grpc-kotlin:grpc-kotlin-app", "grpc-kotlin:grpc-kotlin-spring")
