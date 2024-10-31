import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
}

group = "org.example"
version = "1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// https://stackoverflow.com/questions/67795324/gradle7-version-catalog-how-to-use-it-with-buildsrc
val libs = the<LibrariesForLibs>()

dependencies {
    testImplementation(libs.junit.jupiter)
    constraints {
        implementation(libs.guava)
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<DependencyReportTask>("allDeps") {}
