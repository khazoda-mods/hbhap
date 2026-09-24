pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.7"
}

stonecutter {
    create(rootProject) {
        version("26.1", "26.1")
        version("26.2", "26.2")
        version("26.3", "26.3")
        vcsVersion = "26.1"
    }
}

rootProject.name = "hbhap"
