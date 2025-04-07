val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation(project(":common"))
    implementation(project(":grpc-java:grpc-java-proto"))

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter")

    mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
}

tasks.withType<Test> {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}
