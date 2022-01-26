import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.springframework.boot") version "2.6.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "com.veosps.rsrendering"
version = "1.0.0"

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

springBoot {
    mainClass.set("com.veosps.rsrendering.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("com.displee:rs-cache-library:6.8.1")
    implementation("io.netty:netty-buffer:4.1.73.Final")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger-jvm:1.0.3")
    implementation("com.michael-bull.kotlin-result:kotlin-result-jvm:1.1.14")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")

    implementation("org.joml:joml:1.10.3")
    implementation("org.lwjgl:lwjgl:3.3.0")
    implementation("org.lwjgl:lwjgl-stb:3.3.0")
    implementation("org.lwjgl:lwjgl-glfw:3.3.0")
    implementation("org.lwjgl:lwjgl-opengl:3.3.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val lwjglNatives = when (OperatingSystem.current()) {
    OperatingSystem.LINUX   -> System.getProperty("os.arch").let {
        if (it.startsWith("arm") || it.startsWith("aarch64"))
            "natives-linux-${if (it.contains("64") || it.startsWith("armv8")) "arm64" else "arm32"}"
        else
            "natives-linux"
    }
    OperatingSystem.MAC_OS  -> if (System.getProperty("os.arch").startsWith("aarch64")) "natives-macos-arm64" else "natives-macos"
    OperatingSystem.WINDOWS -> System.getProperty("os.arch").let {
        if (it.contains("64"))
            "natives-windows${if (it.startsWith("aarch64")) "-arm64" else ""}"
        else
            "natives-windows-x86"
    }
    else -> throw Error("Unrecognized or unsupported Operating system. Please set \"lwjglNatives\" manually")
}