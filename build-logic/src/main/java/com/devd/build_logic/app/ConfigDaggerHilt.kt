package com.devd.build_logic.app

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.configureDaggerHilt(){
    pluginManager.apply("kotlin-kapt")
    pluginManager.apply("com.google.devtools.ksp")
    pluginManager.apply("com.google.dagger.hilt.android")
    dependencies{
        val libs = project.extensions.libs
        "implementation"(libs.findLibrary("dagger.hilt.android").get())
        "implementation"(libs.findLibrary("androidx.hilt.navigation.compose").get())
        "kapt"(libs.findLibrary("dagger.hilt.android.compiler").get())
    }

}