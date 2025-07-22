import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.1.10"
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "de.animegamingcafe"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("de.animegamingcafe.entbannungssystem.Main")
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.github.freya022:BotCommands:3.0.0-beta.1")
    implementation("net.dv8tion:JDA:5.3.0")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.17")
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    runtimeOnly("dev.reformator.stacktracedecoroutinator:stacktrace-decoroutinator-common:2.4.8")
    implementation("dev.reformator.stacktracedecoroutinator:stacktrace-decoroutinator-jvm:2.4.8")
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("AGCEntbannung.jar")
    manifest {
        attributes["Main-Class"] = "de.animegamingcafe.entbannungssystem.Main"
    }
}