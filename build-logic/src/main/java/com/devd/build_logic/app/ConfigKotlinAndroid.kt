package com.devd.build_logic.app

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

fun Project.configureKotlinAndroid() {

    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())

    val ociBuketKey = properties.getProperty("oracle.buket.key")
    val admobServiceId = properties.getProperty("admob.service.id")
    val admobBannerId = properties.getProperty("admob.banner.id")
    val admobInterstitialId = properties.getProperty("admob.interstitial.id")

//    Plugins
    pluginManager.apply("org.jetbrains.kotlin.android")
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
    pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
    androidExtension.apply {
        compileSdk = 36
        defaultConfig {
            minSdk = 29
            buildConfigField("String","OCI_BUCKET_KEY",ociBuketKey)
            resValue("string", "admobServiceId", admobServiceId)
            resValue("string", "admobBannerId", admobBannerId)
            resValue("string", "admobInterstitialId", admobInterstitialId)
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        buildFeatures {
            buildConfig = true
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.15"
        }

    }


    configureKotlin()

    val libs = project.extensions.libs
    dependencies {
        "implementation"(libs.findBundle("androidx.datastore.bundle").get())
        "implementation"(libs.findLibrary("kotlinx.serialization.json").get())
    }

}

internal fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            val warningAsErrors: String? by project
            allWarningsAsErrors.set(warningAsErrors.toBoolean())
//            val new = freeCompilerArgs
//            new.add("-opt-in=kotlin.RequiresOptIn")
            freeCompilerArgs.addAll("-Xopt-in=kotlin.RequiresOptIn")
        }
//        kotlinOptions{
//            jvmTarget = JavaVersion.VERSION_1_8.toString()
//
//        }


    }
}