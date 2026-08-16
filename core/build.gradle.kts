plugins {
    id("java-library")
}

description = "Base-Project for Media-Access"

dependencies {
    api("org.springframework.boot:spring-boot-starter-jdbc") {
        // exclude(group = "ch.qos.logback", module = "logback-classic")
    }
    api("org.xerial:sqlite-jdbc")

    implementation("tools.jackson.dataformat:jackson-dataformat-csv")

    runtimeOnly("org.slf4j:jcl-over-slf4j")
    runtimeOnly("org.slf4j:slf4j-simple")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
}

val mainClazz = "de.freese.mediathek.report.Reporter"

// Start: gradle runReporter
tasks.register<JavaExec>("runReporter") {
    group = "MyTasks"
    description = "Run the main class with JavaExecTask"

    // executable = ".../java.exe"
    // workingDir = workDir
    // args("...","...")
    // jvmArgs("...","...")
    // debugOptions {
    //     enabled = true
    //     port = 5566
    //     server = true
    //     suspend = false
    // }

    // classpath = files(...)
    // classpath = configurations.runtimeClasspath // Doesn't contain Main-Class !
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set(mainClazz)

    // args("-Dspring.profiles.active=file")
    // environment("spring.profiles.active", "file")
}
