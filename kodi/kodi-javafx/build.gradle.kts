plugins {
    id("java")
    id("org.springframework.boot")
    id("org.openjfx.javafxplugin")
}

description = "JavaFX GUI for KODI"

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
    modules = listOf("javafx.controls", "javafx.fxml")
    configuration = "implementation"
    // platform = "linux" // linux, windows, mac
    // sdk = "PATH"
}

dependencies {
    implementation(project(":kodi:kodi-core"))
}

// Start: gradle bootRun --args="--spring.profiles.active=dev"
// The archive name. If the name has not been explicitly set, the pattern for the name is:
// [archiveBaseName]-[archiveAppendix]-[archiveVersion]-[archiveClassifier].[archiveExtension]
// archiveFileName = "my-boot.jar"
springBoot {
    mainClass = "de.freese.mediathek.kodi.javafx.KodiJavaFxClientLauncher"
}

//tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
//    // Holt das bootJar-Task-Objekt aus dem Core-Projekt und fügt es als File-Input hinzu
//    inputs.files(project(":kodi:kodi-core").tasks.named("bootJar"))
//}