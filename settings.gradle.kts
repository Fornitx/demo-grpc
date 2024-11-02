pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
}

rootProject.name = "demo-grpc2"

include("common")
include("grpc-java", "grpc-java:grpc-java-proto", "grpc-java:grpc-java-app")
include("grpc-kotlin", "grpc-kotlin:grpc-kotlin-proto", "grpc-kotlin:grpc-kotlin-app", "grpc-kotlin:grpc-kotlin-spring")
findProject(":grpc-java:grpc-java-proto")?.name = "grpc-java-proto"
findProject(":grpc-java:grpc-java-app")?.name = "grpc-java-app"
findProject(":grpc-kotlin:grpc-kotlin-proto")?.name = "grpc-kotlin-proto"
findProject(":grpc-kotlin:grpc-kotlin-app")?.name = "grpc-kotlin-app"
findProject(":grpc-kotlin:grpc-kotlin-spring")?.name = "grpc-kotlin-spring"
