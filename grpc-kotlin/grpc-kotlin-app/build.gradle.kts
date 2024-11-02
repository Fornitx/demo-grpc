dependencies {
    implementation(project(":common"))
    implementation(project(":grpc-kotlin:grpc-kotlin-proto"))

    implementation(libs.grpc.netty.shaded)

    testImplementation(platform(libs.spring.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation(libs.mockito.kotlin)
}
