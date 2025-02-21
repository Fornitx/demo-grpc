plugins {
    alias(libs.plugins.kotlin.jvm)
}

subprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }
}
