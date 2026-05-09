import com.vanniktech.maven.publish.DeploymentValidation
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.plugins.signing.Sign
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.vanniktech.maven.publish) apply false
}

val publishGroup = "io.github.vickyleu.swiperefresh"
val publishVersion = "2.0.0"
val publishRepo = "UltraSwipeRefresh"
val publishUrl = "https://github.com/vickyleu/$publishRepo"
val publishedProjects = setOf(
    "refresh",
    "refresh-indicator-classic",
    "refresh-indicator-lottie",
    "refresh-indicator-progress",
)

allprojects {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.add("-Xopt-in=kotlin.RequiresOptIn")
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }
}

subprojects {
    if (name !in publishedProjects) return@subprojects

    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.vanniktech.maven.publish")

    extensions.configure<org.jetbrains.dokka.gradle.DokkaExtension>("dokka") {
        moduleName.set(name)
        dokkaPublications.named("html") {
            offlineMode.set(false)
            moduleName.set(name)
        }
        dokkaSourceSets.configureEach {
            reportUndocumented.set(false)
            enableAndroidDocumentationLink.set(true)
            enableKotlinStdLibDocumentationLink.set(true)
            enableJdkDocumentationLink.set(true)
            jdkVersion.set(libs.versions.jvmTarget.get().toInt())
        }
    }

    extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
        coordinates(publishGroup, name, publishVersion)
        publishToMavenCentral(
            automaticRelease = true,
            validateDeployment = DeploymentValidation.PUBLISHED,
        )
        signAllPublications()

        pom {
            name.set("Vickyleu KMP UltraSwipeRefresh $name")
            description.set("Compose Multiplatform swipe refresh component.")
            inceptionYear.set("2026")
            url.set(publishUrl)
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("vickyleu")
                    name.set("Vickyleu")
                    url.set("https://github.com/vickyleu")
                }
            }
            scm {
                url.set(publishUrl)
                connection.set("scm:git:https://github.com/vickyleu/$publishRepo.git")
                developerConnection.set("scm:git:ssh://git@github.com/vickyleu/$publishRepo.git")
            }
            issueManagement {
                system.set("GitHub")
                url.set("$publishUrl/issues")
            }
            ciManagement {
                system.set("GitHub Actions")
                url.set("$publishUrl/actions")
            }
        }
    }

    tasks.withType<Sign>().configureEach {
        onlyIf {
            val hasSigningKey =
                providers.gradleProperty("signingInMemoryKey").isPresent ||
                    providers.gradleProperty("signing.secretKeyRingFile").isPresent
            val publishingToCentral = gradle.taskGraph.allTasks.any { task ->
                task.name.contains("MavenCentral")
            }
            hasSigningKey || publishingToCentral
        }
    }
}
