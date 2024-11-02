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
        implementation(rootProject.libs.kotlin.reflect)
        testImplementation(rootProject.libs.kotlin.test.junit5)
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
