// Can not be configured by Conventions-Plugin.
pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

// Without rootProject.name the Name of the Projekt-Directory is used.
// rootProject.name = "mediathek"

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
