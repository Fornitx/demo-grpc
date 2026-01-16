plugins {
    java
    alias(libs.plugins.spring.boot) apply false
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
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            mavenBom(rootProject.libs.spring.grpc.bom.get().toString())
        }
    }

    ext["kotlin.version"] = rootProject.libs.versions.kotlin.lang.get()

    tasks.test {
        useJUnitPlatform()
    }
}
