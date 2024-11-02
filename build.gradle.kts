allprojects {
    group = "org.example"
    version = "1.0"
}

subprojects {
    tasks.register<DependencyReportTask>("allDeps") {}
}
