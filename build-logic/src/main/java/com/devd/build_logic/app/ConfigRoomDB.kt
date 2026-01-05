package com.devd.build_logic.app

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies


fun Project.configureRoomDBAndroid(){

    pluginManager.apply("org.jetbrains.kotlin.kapt")

    val libs = project.extensions.libs
    dependencies{
        "implementation"(libs.findLibrary("androidx.room.runtime").get())
        "kapt"(libs.findLibrary("androidx.room.compiler").get())
        "kapt"(libs.findLibrary("androidx.room.compiler.processing").get())
        "implementation"(libs.findLibrary("androidx.room.ktx").get())
    }

}

class AndroidRoomPlugin : Plugin<Project>{
    override fun apply(target: Project) {
        with(target){
            configureRoomDBAndroid()
        }
    }
}