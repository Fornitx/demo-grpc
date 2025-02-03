plugins {
    alias(libs.plugins.kotlin.jvm)
}

subprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)

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
}
