plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

group = "me.darkcube"
version = "2.4.0"

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

    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.xerial:sqlite-jdbc:3.45.3.0")
    implementation("com.mysql:mysql-connector-j:8.3.0")

    implementation("org.bstats:bstats-bukkit:3.2.1")

    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.10")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.10.0")
}

tasks.shadowJar {
    archiveFileName.set("WastelandArtifacts-${project.version}.jar")
    relocate("org.bstats", "${project.group}.libs.bstats")
    mergeServiceFiles()
    minimize {
        exclude(dependency("com.fasterxml.jackson.*:.*:.*"))
        exclude(dependency("com.zaxxer:.*:.*"))
        exclude(dependency("org.xerial:.*:.*"))
        exclude(dependency("com.mysql:.*:.*"))
        exclude(dependency("net.kyori:.*:.*"))
        exclude(dependency("org.bstats:.*:.*"))
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
        dependsOn(shadowJar)
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}
