import me.modmuss50.mpp.ReleaseType
import me.modmuss50.mpp.platforms.modrinth.ModrinthEnvironment

plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "2.1.1"
}

stonecutter active file(".sc_active_version")

stonecutter parameters {
    properties {
        tags(current.version)
    }
}

val versionTargets = stonecutter.tree.nodes.map { node ->
    node.metadata.version to stonecutter.properties.raw(node.metadata.version, "mod", "mc_releases").asList()
        .map { it.toString() }
}
val availableVersions = versionTargets.map { it.first }.toSet()
val publishVersions = providers.environmentVariable("PUBLISH_VERSIONS").orNull
    ?.takeUnless { it.isBlank() }
    ?.split(Regex("[,\\s]+"))
    ?.filter { it.isNotBlank() }
    ?.toSet()
    ?: availableVersions
require(publishVersions.all { it in availableVersions }) {
    "Unknown publish versions: ${(publishVersions - availableVersions).joinToString()}"
}
val publishTargets = versionTargets.filter { it.first in publishVersions }
require(publishTargets.isNotEmpty()) { "PUBLISH_VERSIONS did not select any Minecraft versions" }

val modSlug = property("mod.slug") as String
val modName = property("mod.name") as String
val modVersion = property("mod.version") as String
val artifactDirectory = layout.buildDirectory.dir("libs/$modVersion")
val publishCurseForge = providers.gradleProperty("publishCurseForge").map(String::toBoolean).orElse(true).get()
val publishModrinth = providers.gradleProperty("publishModrinth").map(String::toBoolean).orElse(true).get()

fun artifactVersion(version: String) = "$modVersion+$version"
fun artifactProvider(version: String) = artifactDirectory.map {
    it.file("$modSlug-${artifactVersion(version)}.jar")
}

val buildAll = tasks.register<Sync>("buildAll") {
    group = "build"
    description = "Builds and collects one Fabric/NeoForge jar per Minecraft version"
    into(artifactDirectory)
}

gradle.projectsEvaluated {
    buildAll.configure {
        val jars = subprojects.map { it.tasks.named<Jar>("jar") }
        dependsOn(jars)
        from(jars.map { it.flatMap(Jar::getArchiveFile) })
    }
}

publishMods {
    changelog.set(providers.environmentVariable("CHANGELOG"))
    version.set(modVersion)
    displayName.set("$modName $modVersion")
    type.set(ReleaseType.STABLE)
    dryRun.set(providers.gradleProperty("publishDryRun").map(String::toBoolean).orElse(false))

    publishTargets.forEach { (mcVersion, mcReleases) ->
        val targetVersion = artifactVersion(mcVersion)
        val artifact = artifactProvider(mcVersion)
        val taskSuffix = mcVersion.replace('.', '_')

        if (publishCurseForge) {
            curseforge("curseforge$taskSuffix") {
                projectId.set(providers.environmentVariable("CURSEFORGE_PROJECT_ID"))
                accessToken.set(providers.environmentVariable("CURSEFORGE_API_KEY"))
                file.set(artifact)
                version.set(targetVersion)
                displayName.set("$modName $targetVersion")
                modLoaders.addAll(listOf("fabric", "neoforge"))
                minecraftVersions.addAll(mcReleases)
                javaVersions.add(JavaVersion.VERSION_25)
                client.set(true)
                server.set(false)
            }
        }

        if (publishModrinth) {
            modrinth("modrinth$taskSuffix") {
                projectId.set(providers.environmentVariable("MODRINTH_PROJECT_ID"))
                accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
                file.set(artifact)
                version.set(targetVersion)
                displayName.set("$modName $targetVersion")
                modLoaders.addAll(listOf("fabric", "neoforge"))
                minecraftVersions.addAll(mcReleases)
                environment.set(ModrinthEnvironment.CLIENT_ONLY)
            }
        }
    }
}
