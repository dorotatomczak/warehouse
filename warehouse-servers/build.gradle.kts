group = "com.dorotatomczak"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.4.10"
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("plugin.spring") version "1.3.72"
}

allprojects {
    apply<org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin>()

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))

        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation(group = "org.springframework.boot", name = "spring-boot-starter-data-jpa")

        // Crypto
        implementation(group = "org.springframework.security", name = "spring-security-crypto", version = "5.4.1")

        // DB
        runtimeOnly(group = "com.h2database", name = "h2", version = "1.4.200")
        implementation(group = "org.liquibase", name = "liquibase-core", version = "4.1.0")

        testImplementation("org.springframework.boot:spring-boot-starter-test") {
            exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        }
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
        test {
            useJUnitPlatform()
        }
    }
}
