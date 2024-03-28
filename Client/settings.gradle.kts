
rootProject.name = "Client"
include(":Server")
project(":Server").projectDir = file("../Server")

pluginManagement {
    plugins {
        kotlin("jvm") version "1.9.23"
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
    }
}