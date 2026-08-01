plugins {
    id("java")
    id("org.openjfx.javafxplugin")
}

javafx {
    version = property("version_javafx").toString()
    modules = listOf("javafx.media", "javafx.swing")
    configuration = "implementation"
}

dependencies {
    implementation(project(":music-player:player-core"))
    implementation(project(":music-player:player-equalizer"))
    implementation(project(":music-player:player-fft"))

    implementation("com.github.kwhat:jnativehook")
    implementation("net.jthink:jaudiotagger")
    implementation("org.slf4j:jul-to-slf4j")

    runtimeOnly("org.slf4j:slf4j-simple")

}
