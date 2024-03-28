import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "battleship.server"
version = "0.0.1"

application {
    mainClass.set("battleship.server.program.MainServerKt")
}

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "2.3.9"
    api("io.github.microutils:kotlin-logging-jvm:3.0.5")
    api("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    api("ch.qos.logback:logback-classic:1.5.3")
    api("io.ktor:ktor-serialization:$ktorVersion")
    api("io.ktor:ktor-websockets:$ktorVersion")
    api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

buildscript {
    repositories {
        mavenCentral()
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}