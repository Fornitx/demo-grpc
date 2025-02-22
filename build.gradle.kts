plugins {
    java
    alias(libs.plugins.spring.dm)
}

val springBom = libs.spring.bom.get().toString()
val springGrpcBom = libs.spring.grpc.bom.get().toString()

val grpcVersion = libs.versions.grpc.core.get().toString()

allprojects {
    group = "org.example"
    version = "1.0"

    tasks.register<DependencyReportTask>("allDeps") {}
}

subprojects {
    apply(plugin = "java")
    apply(plugin = rootProject.libs.plugins.spring.dm.get().pluginId)

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
