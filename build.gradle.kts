/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("org.bstats:bstats-bukkit:2.2.1")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-platform-bukkit:4.3.2")
    compileOnly("me.clip:placeholderapi:2.11.5")
}
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
group = "net.multylands.duels"
version = "v1.32"
description = "Duels"

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
tasks {
    shadowJar {
        // See https://github.com/johnrengelman/shadow/issues/448
        project.configurations.implementation.get().isCanBeResolved = true

        configurations = listOf(project.configurations.runtimeClasspath.get())
        relocate("org.bstats", project.group.toString())
    }
}
tasks.processResources {
    outputs.upToDateWhen { false }
    filesMatching("plugin.yml") {
        expand(mapOf("version" to project.version))
    }
}
tasks {
    runServer {
        downloadPlugins {
            modrinth("simple-fly", "nIB1yFFr")
            modrinth("simplescore", "GK5oIgBS")
            url("https://hangarcdn.papermc.io/plugins/HelpChat/PlaceholderAPI/versions/2.11.5/PAPER/PlaceholderAPI-2.11.5.jar")
        }
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.20.4")
    }
}
