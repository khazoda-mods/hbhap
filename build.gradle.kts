plugins {
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.slug") as String

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    implementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
    runs.named("client") {
        displayName.set("Fabric Client ${sc.current.version}")
        appendProjectPathToDisplayName.set(false)
        generateRunConfig.set(true)
        preferGradleTask.set(true)
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    jar {
        from(rootProject.file("LICENSE"))
    }

    compileJava {
        dependsOn("stonecutterGenerate")
    }

    processResources {
        dependsOn("stonecutterGenerate")
        fun property(name: String): String = sc.properties[name]
        val props = mapOf(
            "id" to property("mod.id"),
            "name" to property("mod.name"),
            "version" to property("mod.version"),
            "fabric_minecraft" to property("mod.mc_compat_fabric"),
            "neoforge_minecraft" to property("mod.mc_compat_neoforge"),
            "description" to property("mod.description"),
            "authors" to property("mod.authors"),
            "license" to property("mod.license"),
            "homepage" to property("mod.homepage"),
            "sources" to property("mod.sources"),
            "issues" to property("mod.issues"),
            "fabric_loader" to property("deps.fabric_loader"),
            "neoforge_loader" to property("deps.neoforge_loader")
        )
        inputs.properties(props)
        filesMatching("fabric.mod.json") { expand(props) }
        filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
    }

}
