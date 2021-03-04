plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
}

dependencies {
    // Resource server
    implementation(group = "org.springframework.cloud", name = "spring-cloud-starter-oauth2", version = "2.2.4.RELEASE")
    implementation(group = "org.springframework.cloud", name = "spring-cloud-starter-security", version = "2.2.4.RELEASE")
}
