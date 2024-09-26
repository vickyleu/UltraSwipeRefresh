@file:Suppress("DSL_SCOPE_VIOLATION")

import com.android.build.gradle.LibraryExtension
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.HttpURLConnection
import java.net.URL
import java.util.Properties
import java.util.concurrent.Executors


// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.dokka)
    id("maven-publish")
}

allprojects {
    tasks.withType<KotlinCompile>().all {
        compilerOptions {
            freeCompilerArgs.add("-Xopt-in=kotlin.RequiresOptIn")
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }
}


val javaVersion = JavaVersion.toVersion(libs.versions.jvmTarget.get())
check(JavaVersion.current().isCompatibleWith(javaVersion)) {
    "This project needs to be run with Java ${javaVersion.getMajorVersion()} or higher (found: ${JavaVersion.current()})."
}


buildscript {
    dependencies {
        val dokkaVersion = libs.versions.dokka.get()
        classpath("org.jetbrains.dokka:dokka-base:$dokkaVersion")
        classpath("org.jetbrains.compose:compose-gradle-plugin:${libs.versions.compose.plugin.get()}")
    }
}



allprojects {
    val properties = Properties().apply {
        runCatching { rootProject.file("local.properties") }
            .getOrNull()
            .takeIf { it?.exists() ?: false }
            ?.reader()
            ?.use(::load)
    }
    val environment: Map<String, String?> = System.getenv()
    val myExtra = mutableMapOf<String, Any>()
    myExtra["githubToken"] = properties["github.token"] as? String
        ?: environment["GITHUB_TOKEN"] ?: ""

    val libs = rootDir.resolve("gradle/libs.versions.toml")
    val map = hashMapOf<String, String>()
    libs.useLines {
        it.forEach { line ->
            if (line.contains("=") && line.replace(" ", "").startsWith("#").not()) {
                val (key, value) = line.split("=")
                map[key.replace(" ", "").removeSurrounding("\"")] =
                    value.replace(" ", "").removeSurrounding("\"")
            }
        }
    }
    val jvmTarget = map["jvmTarget"] ?: "11"
    val rootProjectName = rootDir.name.lowercase()
        .replace("compose-", "")
        .replace("compose_", "")
        .replace("compose", "")
        .replace("ultra", "")

    val mavenAuthor = "vickyleu"
    val mavenGroup = "com.$mavenAuthor.$rootProjectName"
    val mVersion = "1.0.2"
    if (project.subprojects.isNotEmpty()) return@allprojects
    val currentName = project.name.replace("$rootProjectName-", "")
    if (rootProject.name == currentName) {
        return@allprojects
    }
    if (project.name.contains("composeApp") ||project.name.contains("app") ||project.name.contains("sample") || project.parent?.name?.contains("sample") == true) {
        return@allprojects
    }
    println("currentName: ${project.parent?.name} - ${project.name}")

    val mGroup = mavenGroup
    project.apply(plugin = "org.jetbrains.dokka")
    project.apply(plugin = "maven-publish")
    project.afterEvaluate {
        if (project.extensions.findByName("android") != null) {
            val ext = project.extensions.findByType<LibraryExtension>()
                ?: project.extensions.findByType<com.android.build.gradle.AppExtension>()
            if (ext != null && ext is LibraryExtension) {
            } else {
                return@afterEvaluate
            }
        } else {
            return@afterEvaluate
        }


        tasks.withType<PublishToMavenRepository> {
            val isMac = DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX
            onlyIf {
                isMac.also {
                    if (!isMac) logger.error(
                        """
                    Publishing the library requires macOS to be able to generate iOS artifacts.
                    Run the task on a mac or use the project GitHub workflows for publication and release.
                """,
                    )
                }
            }
        }
        val javadocJar by tasks.registering(Jar::class) {
            dependsOn(tasks.dokkaHtml)
            from(tasks.dokkaHtml.flatMap(org.jetbrains.dokka.gradle.DokkaTask::outputDirectory))
            archiveClassifier = "javadoc"
        }
        publishing {
            val projectName = rootDir.name
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/$mavenAuthor/${projectName}")
                    credentials {
                        username = "$mavenAuthor"
                        password = myExtra["githubToken"]?.toString()
                    }
                }
            }
            val shouldRegistering = when{
                project.extensions.findByName("javaPlatform")!=null->true
                project.extensions.findByType(org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension::class) != null->false
                project.extensions.findByName("android")!=null -> {
                    true
                }
                else->false
            }
            afterEvaluate {
                /*if(shouldRegistering){
                    publications.register<MavenPublication>(
                        if(project.extensions.findByName("android")!=null) "release" else "java")
                    {

                        when{
                            project.extensions.findByName("javaPlatform")!=null->{
                                from(components["javaPlatform"])
                            }
                            project.extensions.findByName("android")!=null->{
                                from(components["release"])
                            }
                            else->Unit
                        }
                        version = mVersion
                        groupId = mGroup
                        if (artifactId.startsWith("${rootProjectName}-${currentName}")) {
                            artifactId =
                                artifactId.replace("${rootProjectName}-${currentName}", currentName)
                        }
                        artifactId = artifactId.lowercase()
                        println("artifactId: $artifactId \"${rootProjectName}-${currentName}\"")
                        pom {
                            url = "https://github.com/$mavenAuthor/${projectName}"
                            name = projectName
                            description = """
                Visit the project on GitHub to learn more.
            """.trimIndent()
                            inceptionYear = "2024"
                            licenses {
                                license {
                                    name = "Apache-2.0 License"
                                    url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                                }
                            }
                            developers {
                                developer {
                                    id = "jenly1314"
                                    name = "jenly1314"
                                    email = ""
                                    roles = listOf("Mobile Developer")
                                    timezone = "GMT+8"
                                }
                            }
                            contributors {
                                // contributor {}
                            }
                            scm {
                                tag = "HEAD"
                                url = "https://github.com/$mavenAuthor/${projectName}"
                                connection = "scm:git:github.com/$mavenAuthor/${projectName}.git"
                                developerConnection =
                                    "scm:git:ssh://github.com/$mavenAuthor/${projectName}.git"
                            }
                            issueManagement {
                                system = "GitHub"
                                url = "https://github.com/$mavenAuthor/${projectName}/issues"
                            }
                            ciManagement {
                                system = "GitHub Actions"
                                url = "https://github.com/$mavenAuthor/${projectName}/actions"
                            }
                        }
                    }
                }else{*/
                    publications.withType<MavenPublication> {
                        artifact(javadocJar)
                        version = mVersion
                        groupId = mGroup
                        if (artifactId.startsWith("${rootProjectName}-${currentName}")) {
                            artifactId =
                                artifactId.replace("${rootProjectName}-${currentName}", currentName)
                        }
                        artifactId = artifactId.lowercase()
                        println("artifactId: $artifactId \"${rootProjectName}-${currentName}\"")
                        pom {
                            url = "https://github.com/$mavenAuthor/${projectName}"
                            name = projectName
                            description = """
                Visit the project on GitHub to learn more.
            """.trimIndent()
                            inceptionYear = "2024"
                            licenses {
                                license {
                                    name = "Apache-2.0 License"
                                    url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                                }
                            }
                            developers {
                                developer {
                                    id = "jenly1314"
                                    name = "jenly1314"
                                    email = ""
                                    roles = listOf("Mobile Developer")
                                    timezone = "GMT+8"
                                }
                            }
                            contributors {
                                // contributor {}
                            }
                            scm {
                                tag = "HEAD"
                                url = "https://github.com/$mavenAuthor/${projectName}"
                                connection = "scm:git:github.com/$mavenAuthor/${projectName}.git"
                                developerConnection =
                                    "scm:git:ssh://github.com/$mavenAuthor/${projectName}.git"
                            }
                            issueManagement {
                                system = "GitHub"
                                url = "https://github.com/$mavenAuthor/${projectName}/issues"
                            }
                            ciManagement {
                                system = "GitHub Actions"
                                url = "https://github.com/$mavenAuthor/${projectName}/actions"
                            }
                        }
                    }
                /*}*/
            }
        }
        tasks.dokkaHtml {
            // outputDirectory = layout.buildDirectory.get().resolve("dokka")
            offlineMode = false
            moduleName = currentName
            // See the buildscript block above and also
            // https://github.com/Kotlin/dokka/issues/2406
//    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
////        customAssets = listOf(file("../asset/logo-icon.svg"))
////        customStyleSheets = listOf(file("../asset/logo-styles.css"))
//        separateInheritedMembers = true
//    }

            dokkaSourceSets {
                configureEach {
                    reportUndocumented = true
                    noAndroidSdkLink = false
                    noStdlibLink = false
                    noJdkLink = false
                    jdkVersion = jvmTarget.toInt()
                    // sourceLink {
                    //     // Unix based directory relative path to the root of the project (where you execute gradle respectively).
                    //     // localDirectory.set(file("src/main/kotlin"))
                    //     // URL showing where the source code can be accessed through the web browser
                    //     // remoteUrl = uri("https://github.com/mahozad/${project.name}/blob/main/${project.name}/src/main/kotlin").toURL()
                    //     // Suffix which is used to append the line number to the URL. Use #L for GitHub
                    //     remoteLineSuffix = "#L"
                    // }
                }
            }
        }
        tasks.withType(AbstractPublishToMaven::class).configureEach {
            dependsOn(tasks.withType(Sign::class))
        }
    }
}


tasks.register("deletePackages") {

    val libs = rootDir.resolve("gradle/libs.versions.toml")
    val map = hashMapOf<String, String>()
    libs.useLines {
        it.forEach { line ->
            if (line.contains("=") && line.startsWith("#").not()) {
                val (key, value) = line.split("=")
                map[
                    key
                        .replace(" ", "").removeSurrounding("\""),
                ] =
                    value
                        .replace(" ", "").removeSurrounding("\"")
            }
        }
    }

    val rootProjectName = rootDir.name.lowercase()
        .replace("compose-", "")
        .replace("compose_", "")
        .replace("compose", "")
        .replace("ultra", "")


    val mavenAuthor = "vickyleu"
    val mavenGroup = "com.$mavenAuthor.$rootProjectName"
//    val mavenGroup = "peekaboo"

    group = "publishing"
    description = "Delete all packages in the GitHub Packages registry"


    val keyword = "${mavenGroup}"
    println("keyword: $keyword")
    val properties = Properties().apply {
        runCatching { rootProject.file("local.properties") }
            .getOrNull()
            .takeIf { it?.exists() ?: false }
            ?.reader()
            ?.use(::load)
    }
// For information about signing.* properties,
// see comments on signing { ... } block below
    val environment: Map<String, String?> = System.getenv()
    val myExtra = mutableMapOf<String, Any>()
    myExtra["githubToken"] = properties["github.token"] as? String
        ?: environment["GITHUB_TOKEN"] ?: ""
    val headers = mapOf(
        "Accept" to "application/vnd.github.v3+json",
        "Authorization" to "Bearer ${myExtra["githubToken"]}",
        "X-GitHub-Api-Version" to "2022-11-28",
    )
    doLast {
        runBlocking {
            val executor = Executors.newFixedThreadPool(10)
            val scope = CoroutineScope(executor.asCoroutineDispatcher())
            val fetchJobs = packageTypes.flatMap { packageType ->
                visibilityTypes.map { visibility ->
                    scope.async {
                        fetchPackages(packageType, visibility, headers)
                    }
                }
            }
            fetchJobs.awaitAll().forEach { packages ->
                allPackages.addAll(packages)
            }
            val deleteJobs = allPackages.filter { pkg ->
                val packageName = pkg["name"] as String
                packageName.contains(keyword)
            }.map { pkg ->
                val packageType = pkg["package_type"] as String
                val packageName = pkg["name"] as String
                scope.async {
                    deletePackage(packageType, packageName, headers)
                }
            }
            try {
                deleteJobs.awaitAll()
                executor.shutdown()
            } catch (e: Exception) {
                println("删除包失败: ${e.message}")
            }
        }
    }
}


val packageTypes = listOf("npm", "maven", "docker", "container")
val visibilityTypes = listOf("public", "private", "internal")
val allPackages = mutableListOf<Map<String, Any>>()

fun fetchPackages(
    packageType: String,
    visibility: String,
    headers: Map<String, String>,
): List<Map<String, Any>> {
    val packages = mutableListOf<Map<String, Any>>()
    var page = 1

    while (true) {
        val url =
            URL("https://api.github.com/user/packages?package_type=$packageType&visibility=$visibility&page=$page&per_page=100")
        val connection = url.openConnection() as HttpURLConnection

        headers.forEach { (key, value) -> connection.setRequestProperty(key, value) }

        if (connection.responseCode == 200) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val batch: List<Map<String, Any>> = jacksonObjectMapper().readValue(response)
            if (batch.isEmpty()) break
            packages.addAll(batch)
            page++
        } else {
            println("获取$packageType ($visibility) 包列表失败，错误代码: ${connection.responseCode} ${connection.responseMessage}")
            println(connection.inputStream.bufferedReader().use { it.readText() })
            break
        }
    }

    return packages
}

fun deletePackage(packageType: String, packageName: String, headers: Map<String, String>) {
    val url = URL("https://api.github.com/user/packages/$packageType/$packageName")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "DELETE"
    headers.forEach { (key, value) -> connection.setRequestProperty(key, value) }

    if (connection.responseCode == 204 || connection.responseCode == 200) {
        println("$packageName 删除成功")
    } else {
        println("$packageName 删除失败，错误代码: ${connection.responseCode}")
        println(connection.inputStream.bufferedReader().use { it.readText() })
    }
}

