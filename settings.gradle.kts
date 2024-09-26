@file:Suppress("UnstableApiUsage")

rootProject.name = "UltraSwipeRefresh"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories.apply {
        removeAll(this)
    }
    dependencyResolutionManagement.repositories.apply {
        removeAll(this)
    }
    listOf(repositories, dependencyResolutionManagement.repositories).forEach {
        it.apply {
            google()
            gradlePluginPortal()
            mavenCentral()
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}


dependencyResolutionManagement {
    //FAIL_ON_PROJECT_REPOS
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        val properties = java.util.Properties().apply {
            runCatching { rootProject.projectDir.resolve("local.properties") }
                .getOrNull()
                .takeIf { it?.exists() ?: false }
                ?.reader()
                ?.use(::load)
        }
        val environment: Map<String, String?> = System.getenv()
        extra["githubToken"] = properties["github.token"] as? String
            ?: environment["GITHUB_TOKEN"] ?: ""
        maven {
            url = uri("https://maven.pkg.github.com/vickyleu/${rootDir.name}")
            credentials {
                username = "vickyleu"
                password = extra["githubToken"]?.toString()
            }
            content {
                excludeGroupByRegex("com.finogeeks.*")
                excludeGroupByRegex("org.jogamp.*")
                excludeGroupByRegex("org.jetbrains.compose.*")
                excludeGroupByRegex("(?!com|cn).github.(?!vickyleu).*")
            }
        }
        mavenCentral()
        google()
        // workaround for https://youtrack.jetbrains.com/issue/KT-51379
        maven {
            setUrl("https://repo.maven.apache.org/maven2")
            content {
                excludeGroupByRegex("com.finogeeks.*")
                excludeGroupByRegex("org.jogamp.*")
                excludeGroupByRegex("com.vickyleu.*")
                excludeGroupByRegex("com.github.(?!johnrengelman|oshi|bumptech|mzule|pwittchen|filippudak|asyl|florent37).*")
            }
        }



    }
}


include(":composeApp")
include(":refresh")
include(":refresh-indicator-classic")
include(":refresh-indicator-progress")
include(":refresh-indicator-lottie")
