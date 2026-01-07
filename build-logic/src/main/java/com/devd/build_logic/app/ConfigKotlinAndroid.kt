package com.devd.build_logic.app

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

fun Project.configureKotlinAndroid(){

    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())

//    Plugins
    pluginManager.apply("org.jetbrains.kotlin.android")
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
    pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

    androidExtension.apply {
        compileSdk = 36
        defaultConfig {
            minSdk = 29
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        buildFeatures{
            buildConfig = true
            compose = true
        }


    }


    configureKotlin()

    val libs = project.extensions.libs
    dependencies{
        "implementation"(libs.findBundle("androidx.datastore.bundle").get())
        "implementation"(libs.findLibrary("kotlinx.serialization.json").get())
    }

}

internal fun Project.configureKotlin(){
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