dependencies {
    implementation(project(":common"))
    implementation(project(":grpc-kotlin:grpc-kotlin-proto"))

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation(libs.mockito.kotlin)
}
