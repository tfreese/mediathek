plugins {
    id("java")
    id("org.openjfx.javafxplugin")
}

// For JavaFx native-Library Downloads.
configurations.matching { it.isCanBeResolved }.configureEach {
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named<Usage>(Usage.JAVA_RUNTIME))
        attribute(
            OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE,
            objects.named<OperatingSystemFamily>(OperatingSystemFamily.LINUX)
        )
        attribute(
            MachineArchitecture.ARCHITECTURE_ATTRIBUTE,
            objects.named<MachineArchitecture>(MachineArchitecture.X86_64)
        )
    }
}

javafx {
    version = property("version_javafx").toString()
    modules = listOf("javafx.controls")
    configuration = "implementation"
}

dependencies {
    implementation(project(":music-player:player-core"))
    implementation(project(":music-player:player-equalizer"))
    implementation(project(":music-player:player-fft"))

    implementation("com.zaxxer:HikariCP")
    implementation("org.apache.xmlgraphics:batik-transcoder")
    implementation("org.jfree:jfreechart")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.slf4j:slf4j-simple")
}
