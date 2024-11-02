dependencies {
    implementation(project(":common"))
    implementation(project(":grpc-java:grpc-java-proto"))

    implementation(libs.grpc.netty.shaded)

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core")
}
