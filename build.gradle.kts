import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
    id("org.graalvm.buildtools.native") version "0.9.27"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
}

group = "wed0n.csgo"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencyLocking {
    lockAllConfigurations()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-websocket:3.1.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.22")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")
    implementation("org.reflections:reflections:0.10.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.4")
    testImplementation("io.projectreactor:reactor-test:3.5.10")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}