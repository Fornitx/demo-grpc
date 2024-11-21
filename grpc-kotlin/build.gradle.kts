plugins {
    alias(libs.plugins.kotlin.jvm)
}

subprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    dependencies {
        constraints {
            implementation("org.jetbrains.kotlin:kotlin-reflect:" + rootProject.libs.versions.kotlin.lang.get())
        }
    }

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    tasks.test {
        useJUnitPlatform()
    }
}
