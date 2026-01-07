plugins {
    java
    alias(libs.plugins.spring.dm)
}

allprojects {
    group = "org.example"
    version = "1.0"

    tasks.register<DependencyReportTask>("allDeps")

    dependencyLocking {
        lockAllConfigurations()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = rootProject.libs.plugins.spring.dm.get().pluginId)

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    dependencyManagement {
        imports {
            mavenBom(rootProject.libs.spring.bom.get().toString())
            mavenBom(rootProject.libs.spring.grpc.bom.get().toString())
        }
    }

    tasks.test {
        useJUnitPlatform()
    }
}
