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
            implementation(compose.foundation)
            implementation(libs.androidx.annotations)
        }
    }

}
android{
    namespace="com.king.ultraswiperefresh"
    compileSdk= libs.versions.android.compileSdk.get().toInt()
    defaultConfig{
        minSdk=libs.versions.android.minSdk.get().toInt()
    }
    lint{
        targetSdk=libs.versions.android.targetSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
    }
    publishing {
        singleVariant("release"){
            withJavadocJar()
            withSourcesJar()
        }
    }
}
