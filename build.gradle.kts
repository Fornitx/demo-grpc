plugins {
    id("org.springframework.boot") version System.getProperty("spring_version") apply false
    id("io.spring.dependency-management") version System.getProperty("spring_dm_version") apply false
    kotlin("jvm") version System.getProperty("kotlin_version") apply false
    kotlin("plugin.spring") version System.getProperty("kotlin_version") apply false
}

allprojects {
    group = "com.example"
    version = "0.0.1-SNAPSHOT"
}
