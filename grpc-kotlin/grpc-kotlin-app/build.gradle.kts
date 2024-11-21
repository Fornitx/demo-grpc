dependencies {
    implementation(libs.grpc.netty.shaded)

    implementation(project(":common"))
    implementation(project(":grpc-kotlin:grpc-kotlin-proto"))

    implementation(libs.kotlin.reflect)

    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    testImplementation(platform(libs.spring.bom))
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation(libs.mockito.kotlin)
}
