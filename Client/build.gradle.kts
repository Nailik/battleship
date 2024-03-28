import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "battleship.client"
version = "0.0.1"

application {
    mainClass.set("battleship.client.program.SketchKt")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jogamp.org/deployment/maven/")
    }
}

dependencies {
    val ktorVersion = "2.3.9"
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(fileTree(mapOf("dir" to "jogl", "include" to listOf("*.jar"))))

    implementation(project(":Server"))
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    implementation("org.jogamp.gluegen:gluegen-rt-main:2.5.0")
    implementation("org.jogamp.jogl:jogl-all:2.5.0")
}

buildscript {
    repositories {
        mavenCentral()
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}