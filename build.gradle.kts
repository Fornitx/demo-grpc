allprojects {
    group = "org.example"
    version = "1.0"

    tasks.register<DependencyReportTask>("allDeps") {}
}
