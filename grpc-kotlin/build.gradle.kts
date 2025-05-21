plugins {
    alias(libs.plugins.kotlin.jvm)
}

subprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)

    kotlin {
        jvmToolchain(21)
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect:" + rootProject.libs.versions.kotlin1.lang.get())
    }
}

tasks.build {
    enabled = false
}
