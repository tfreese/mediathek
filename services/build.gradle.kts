plugins {
    id("java-library")
}

description = "API for Online-Media-Databases(www.themoviedb.org, www.thetvdb.com)"

dependencies {
    api(project(":core"))
    api("tools.jackson.dataformat:jackson-dataformat-xml")
    api("jakarta.xml.bind:jakarta.xml.bind-api")
    api("org.springframework:spring-web")
}
