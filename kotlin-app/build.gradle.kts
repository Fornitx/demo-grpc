plugins {
    id("buildlogic.kotlin-common-conventions")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":kotlin-proto"))

    implementation(libs.grpc.netty.shaded)

    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.mockito.kotlin)
}
