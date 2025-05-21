plugins {
    alias(libs.plugins.mockito)
}

dependencies {
    implementation(project(":common"))
    implementation(project(":grpc-java:grpc-java-proto"))

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter")
}
