dependencies {
    implementation(project(":common"))
    implementation(project(":grpc-java:grpc-java-proto"))

    implementation(libs.grpc.netty.shaded)

    testImplementation(platform(libs.spring.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter")
}
