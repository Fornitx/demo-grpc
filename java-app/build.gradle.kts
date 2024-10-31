plugins {
    id("buildlogic.java-common-conventions")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":java-proto"))

    implementation(libs.grpc.netty.shaded)

    testImplementation("org.mockito:mockito-core")
}
