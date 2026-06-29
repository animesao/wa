plugins {
    id("java")
}

group = "me.darkcube"
version = "1.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")

    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.10")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.10.0")
}

val fatJar = tasks.register<Jar>("fatJar") {
    archiveFileName.set("WastelandArtifacts-${project.version}.jar")
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith(".jar") && !it.name.contains("paper-api") }
            .map { zipTree(it) }
    }) {
        exclude("META-INF/**")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(mapOf(
            "Main-Class" to "me.darkcube.wa.WastelandArtifacts"
        ))
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    build {
        dependsOn(fatJar)
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}
