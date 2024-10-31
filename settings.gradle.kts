pluginManagement {
    includeBuild("gradle/build-logic")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "demo-grpc"

include("common", "java-proto", "java-app", "kotlin-proto", "kotlin-app")
