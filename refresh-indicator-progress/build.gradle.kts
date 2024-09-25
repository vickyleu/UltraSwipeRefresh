@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins{
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.android.library)
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
    androidTarget{
        publishLibraryVariants("release")
    }
    iosArm64()
    iosX64()
    iosSimulatorArm64()


    sourceSets{
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.compose.bom))
            compileOnly(projects.refresh)
            implementation(compose.foundation)
            implementation(libs.androidx.annotations)
        }
    }
}

android{
    namespace="com.king.ultraswiperefresh.indicator.progress"
    compileSdk= libs.versions.android.compileSdk.get().toInt()
    defaultConfig{
        minSdk=libs.versions.android.minSdk.get().toInt()
    }
    lint{
        targetSdk=libs.versions.android.targetSdk.get().toInt()
    }
    publishing {
        singleVariant("release"){
            withJavadocJar()
            withSourcesJar()
        }
    }
}
