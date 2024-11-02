dependencies {
    implementation(project(":common"))
    implementation(project(":grpc-java:grpc-java-proto"))

    implementation(libs.grpc.netty.shaded)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    testImplementation(platform(libs.spring.bom))
    testImplementation("org.mockito:mockito-junit-jupiter")
}
