plugins {
    kotlin("jvm") version "2.2.21" apply false
    kotlin("plugin.spring") version "2.2.21" apply false
    id("org.springframework.boot") version "4.0.6" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    kotlin("plugin.jpa") version "2.2.21" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2" apply false
}

group = "com.ark"
version = "0.0.1-SNAPSHOT"

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version = "1.5.0"
    }
}
