plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("kapt") version "2.2.21"
    id("com.gradleup.shadow") version "9.1.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "org.slimecraft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation("com.github.SlimifiedxD:quartz:893b16b992")
    kapt("com.github.SlimifiedxD:quartz:893b16b992")

    // Data persistence-related libraries
    implementation("com.j256.ormlite:ormlite-core:6.1")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
    implementation("org.xerial:sqlite-jdbc:3.51.3.0")

    // Plugin dependencies
    implementation(platform("com.intellectualsites.bom:bom-newest:1.56"))
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    // Config library
    implementation("com.sksamuel.hoplite:hoplite-core:3.0.0.RC2")
    implementation("com.sksamuel.hoplite:hoplite-yaml:3.0.0.RC2")
    implementation("com.sksamuel.hoplite:hoplite-watch:3.0.0.RC2")
    testImplementation(kotlin("test"))
}

tasks.shadowJar {
    mergeServiceFiles()
}

kapt {
    correctErrorTypes = true
    includeCompileClasspath = true
}

tasks {
    runServer {
        minecraftVersion("1.21.11")
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}