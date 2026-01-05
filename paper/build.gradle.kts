plugins {
  kotlin("jvm") version "2.2.20"
  id("com.gradleup.shadow") version "9.2.0"
  id("net.kyori.blossom") version "1.2.0"
}

group = "pl.spcode.antilavacast"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven {
    name = "papermc"
    url = uri("https://repo.papermc.io/repository/maven-public/")
  }
}

val pluginName = "AntiLavaCast"

tasks.shadowJar {
  destinationDirectory.set(file("../target"))

  archiveBaseName.set(pluginName)
  archiveClassifier = null

  doLast {
    val pluginsDir = file("./run/paper/plugins")
    if (pluginsDir.exists() && pluginsDir.isDirectory) {
      pluginsDir.listFiles { _, name -> name.startsWith(pluginName) }?.forEach {
        it.delete()
      }

      val builtJar = archiveFile.get().asFile
      copy {
        from(builtJar)
        into(pluginsDir)
      }
    } else {
      logger.warn("run directory does not exist, skipping plugin copy to run directory")
    }
  }
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}
kotlin {
  jvmToolchain(21)
}

// Templating properties like version and project name
tasks.withType<ProcessResources> {
  outputs.upToDateWhen { false }
  filesMatching(listOf("plugin.yml", "paper-plugin.yml")) {
    expand(
        mapOf(
            "version" to version,
            "apiVersion" to "1.19",
            "group" to rootProject.group
        )
    )
  }
}