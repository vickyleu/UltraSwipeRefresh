@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins{
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

kotlin{
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-opt-in=kotlinx.cinterop.ExperimentalForeignApi")
    }
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmTarget.get()))
    }
    applyDefaultHierarchyTemplate()
    androidTarget()
    iosArm64()
    iosX64()
    iosSimulatorArm64()


    sourceSets{
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.compose.bom))
            implementation(compose.foundation)
            implementation(compose.ui)
//            implementation(compose.uiTooling)
            implementation(compose.uiUtil)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)

            implementation(libs.androidx.annotations)
            implementation(libs.navigation.compose)

            implementation(libs.lottie)

            implementation(libs.compose.swiperefresh)
            implementation(libs.compose.swiperefresh.classic)
            implementation(libs.compose.swiperefresh.progress)
            implementation(libs.compose.swiperefresh.lottie)

//            implementation(projects.refresh)
//            implementation(projects.refreshIndicatorClassic)
//            implementation(projects.refreshIndicatorProgress)
//            implementation(projects.refreshIndicatorLottie)

        }
    }
}

android{
    namespace="com.king.ultraswiperefresh.app"
    compileSdk= libs.versions.android.compileSdk.get().toInt()
    sourceSets["main"].apply {
        manifest {
            srcFile("src/androidMain/AndroidManifest.xml")
        }
        res {
            srcDirs("src/androidMain/res")
        }
        resources {
            srcDirs("src/commonMain/resources")
        }
    }

    defaultConfig{
        applicationId="com.king.ultraswiperefresh.app"
        minSdk=libs.versions.android.minSdk.get().toInt()
        versionCode=1
        versionName="1.0"
    }
    packaging{
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    lint{
        targetSdk=libs.versions.android.targetSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
    }
    dependencies{
        implementation(libs.accompanist.swiperefresh)
        debugImplementation(compose.preview)
//        debugImplementation(compose.components.uiToolingPreview)
    }
}
