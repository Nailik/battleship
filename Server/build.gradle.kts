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
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
    implementation("io.ktor:ktor-server-core:1.6.3")
    implementation("io.ktor:ktor-client-cio:1.6.3")
    implementation("io.ktor:ktor-serialization:1.6.3")
    implementation("io.ktor:ktor-websockets:1.6.3")
    implementation("io.ktor:ktor-server-netty:1.6.3")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("io.ktor:ktor-client-serialization:1.6.3")
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