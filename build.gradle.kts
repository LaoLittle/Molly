plugins {
    val kotlinVersion = "1.8.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.13.4"
}

group = "org.laolittle.plugin.molly"
version = "1.4.0"

repositories {
    maven("https://maven.aliyun.com/repository/central")
    mavenCentral()
}

dependencies {
    val ktorVer = "2.2.2"
    implementation("io.ktor:ktor-client:$ktorVer")
    implementation("io.ktor:ktor-client-core:$ktorVer")
    implementation("io.ktor:ktor-client-json:$ktorVer")
    implementation("io.ktor:ktor-client-okhttp:$ktorVer")
}