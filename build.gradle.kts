// Execute Tasks in SubModule: gradle MODULE:clean build
plugins {
    id("de.freese.gradle.conventions").apply(false)
    id("org.openjfx.javafxplugin").apply(false)
    id("org.springframework.boot").apply(false)
}

allprojects {
    plugins.apply("base")
}

subprojects {
    plugins.apply("de.freese.gradle.conventions")

    plugins.withType<JavaPlugin> {
        configurations.configureEach {
            exclude(group = "ch.qos.logback", module = "logback-classic")
        }

        dependencies {
            add("implementation", platform(project(":platform")))

            add("testImplementation", "org.junit.jupiter:junit-jupiter")
            add("testCompileOnly", "org.apiguardian:apiguardian-api")
            add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
//            add("testImplementation", "org.mockito:mockito-junit-jupiter")
        }
    }
}
