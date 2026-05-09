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
            maven("https://maven.aliyun.com/repository/google")
            google()
            gradlePluginPortal()
            mavenCentral()
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}


dependencyResolutionManagement {
    //FAIL_ON_PROJECT_REPOS
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        maven("https://maven.aliyun.com/repository/google")
        mavenCentral()
        google()
        // workaround for https://youtrack.jetbrains.com/issue/KT-51379
        maven {
            setUrl("https://repo.maven.apache.org/maven2")
            content {
                excludeGroupByRegex("com.finogeeks.*")
                excludeGroupByRegex("org.jogamp.*")
                excludeGroupByRegex("com.github.(?!johnrengelman|oshi|bumptech|mzule|pwittchen|filippudak|asyl|florent37).*")
            }
        }



    }
}


//include(":composeApp")
include(":refresh")
include(":refresh-indicator-classic")
include(":refresh-indicator-progress")
include(":refresh-indicator-lottie")
