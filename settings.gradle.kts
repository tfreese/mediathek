// Can not be configured by Conventions-Plugin.
pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }

    val versionMyJavaConventionPlugin = providers.gradleProperty("version_myJavaConventionPlugin")
    val versionSpringDependencyManagementPlugin = providers.gradleProperty("version_springDependencyManagementPlugin")
    val versionSpringBoot = providers.gradleProperty("version_springBoot")
    val versionJavaFxPlugin = providers.gradleProperty("version_javafxPlugin")

    plugins {
        id("de.freese.gradle.conventions").version(versionMyJavaConventionPlugin).apply(false)
        id("io.spring.dependency-management").version(versionSpringDependencyManagementPlugin).apply(false)
        id("org.springframework.boot").version(versionSpringBoot).apply(false)
        id("org.openjfx.javafxplugin").version(versionJavaFxPlugin).apply(false)
    }
}

// Without rootProject.name the Name of the Projekt-Directory is used.
rootProject.name = "mediathek"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

include("core")
include("services")

include("kodi:kodi-core")
include("kodi:kodi-swing")
include("kodi:kodi-javafx")

// include("music-player:jxgrabkey")
include("music-player:player-core")
include("music-player:player-equalizer")
include("music-player:player-fft")
include("music-player:player-test")
include("music-player:player-ui")
