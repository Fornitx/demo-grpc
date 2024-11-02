dependencies {
    implementation(project(":common"))
    implementation(project(":grpc-kotlin:grpc-kotlin-proto"))

    implementation(libs.grpc.netty.shaded)

    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.mockito.kotlin)
}
