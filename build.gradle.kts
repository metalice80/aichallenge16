import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.springframework.boot") version "4.1.1"
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

description = "Minimal synchronous MCP STDIO client"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))
    implementation(platform("io.modelcontextprotocol.sdk:mcp-bom:2.0.1"))
    implementation("io.modelcontextprotocol.sdk:mcp")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.jar {
    enabled = false
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.test {
    useJUnitPlatform {
        excludeTags("integration")
    }
}

val integrationTest = tasks.register<Test>("integrationTest") {
    description = "Runs the real MCP server integration smoke test."
    group = "verification"
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform {
        includeTags("integration")
    }
    maxParallelForks = 1
    shouldRunAfter(tasks.test)
}
