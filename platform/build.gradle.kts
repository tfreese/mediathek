plugins {
    id("java-platform")
}

javaPlatform {
    allowDependencies()
}

// https://docs.gradle.org/current/userguide/platforms.html
dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:" + property("version_springBoot")))
    // api(platform("tools.jackson:jackson-bom:" + property("version_jackson")))

    constraints {
        api("com.github.kwhat:jnativehook:" + property("version_jnativehook"))
        api("org.apache.xmlgraphics:batik-codec:" + property("version_batik"))
        api("org.apache.xmlgraphics:batik-swing:" + property("version_batik"))
        api("org.apache.xmlgraphics:batik-transcoder:" + property("version_batik"))
        api("net.jthink:jaudiotagger:" + property("version_jaudiotagger"))
        api("org.jfree:jfreechart:" + property("version_jfreechart"))

        // api("tools.jackson.dataformat:jackson-dataformat-csv")
    }
}
