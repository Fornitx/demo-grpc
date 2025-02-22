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

rootProject.name = "demo-grpc"

include("common")
include("grpc-java", "grpc-java:grpc-java-proto", "grpc-java:grpc-java-app")
include("grpc-kotlin", "grpc-kotlin:grpc-kotlin-proto", "grpc-kotlin:grpc-kotlin-app", "grpc-kotlin:grpc-kotlin-spring")
