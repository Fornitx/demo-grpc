dependencies {
    implementation(project(":common"))
    implementation(project(":grpc-java:grpc-java-proto"))

    implementation("io.grpc:grpc-netty")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter")
}
