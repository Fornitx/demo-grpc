import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("buildlogic.java-common-conventions")
    id("org.jetbrains.kotlin.jvm")
}

// https://stackoverflow.com/questions/67795324/gradle7-version-catalog-how-to-use-it-with-buildsrc
val libs = the<LibrariesForLibs>()

dependencies {
    implementation(libs.kotlin.reflect)

    testImplementation(libs.kotlin.test)
}

kotlin {
//    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}
