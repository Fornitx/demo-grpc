plugins {
    java
    id("org.gradlex.jvm-dependency-conflict-detection") version "2.1.2"
    alias(libs.plugins.spring.dm)
}

val pluginId = libs.plugins.spring.dm.get().pluginId
val springBom = libs.spring.bom.get().toString()
val springGrpcBom = libs.spring.grpc.bom.get().toString()

val grpcVersion = libs.versions.grpc.core.get().toString()

allprojects {
    apply(plugin = "org.gradlex.jvm-dependency-conflict-detection")

    group = "org.example"
    version = "1.0"

    tasks.register<DependencyReportTask>("allDeps") {}
}

subprojects {
    apply(plugin = "java")
    apply(plugin = pluginId)

    ext["grpc.version"] = grpcVersion

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    dependencyManagement {
        imports {
            mavenBom(springBom)
            mavenBom(springGrpcBom)
        }
    }

    tasks.test {
        useJUnitPlatform()
    }
}
