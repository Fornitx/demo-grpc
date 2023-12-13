dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "demo-grpc"

include("demo-grpc-proto", "demo-grpc-app")
