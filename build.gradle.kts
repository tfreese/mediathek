// Execute Tasks in SubModule: gradle MODULE:clean build
plugins {
    id("de.freese.gradle.conventions").apply(false)
    id("io.spring.dependency-management").apply(false)
    id("org.openjfx.javafxplugin").apply(false)
    id("org.springframework.boot").apply(false)
}

allprojects {
    plugins.apply("base")
}

subprojects {
    plugins.apply("de.freese.gradle.conventions")
    plugins.apply("io.spring.dependency-management")

    val dependencyManagement =
        extensions.getByType<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>()

    extensions.configure(io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension::class.java) {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:" + property("version_springBoot"))
        }

        dependencies {
            dependency("com.github.kwhat:jnativehook:" + property("version_jnativehook"))

            dependencySet("org.apache.xmlgraphics:" + property("version_batik")) {
                entry("batik-codec")
                entry("batik-swing")
                entry("batik-transcoder")
            }

            dependency("net.jthink:jaudiotagger:" + property("version_jaudiotagger"))
            dependency("org.jfree:jfreechart:" + property("version_jfreechart"))
            dependency("tools.jackson.dataformat:jackson-dataformat-csv:" + dependencyManagement.importedProperties["jackson-bom.version"])
        }
    }

    plugins.withType<JavaPlugin> {
        configurations.configureEach {
            exclude(group = "ch.qos.logback", module = "logback-classic")
        }

        dependencies {
            add("testImplementation", "org.junit.jupiter:junit-jupiter")
            add("testCompileOnly", "org.apiguardian:apiguardian-api")
            add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
//            add("testImplementation", "org.mockito:mockito-junit-jupiter")
        }
    }
}
