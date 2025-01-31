plugins {
    id("org.gradlex.jvm-dependency-conflict-detection") version "2.1.2"
}

allprojects {
    apply(plugin = "org.gradlex.jvm-dependency-conflict-detection")

    group = "org.example"
    version = "1.0"

    tasks.register<DependencyReportTask>("allDeps") {}
}
