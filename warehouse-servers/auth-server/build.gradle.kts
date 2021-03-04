plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-oauth2:2.2.4.RELEASE")

    implementation(group = "org.springframework.boot", name = "spring-boot-starter-security")

    implementation(group = "com.google.api-client", name = "google-api-client", version = "1.30.11")
}
