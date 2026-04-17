package com.devd.build_logic.app

import gradle.kotlin.dsl.accessors._7a04235ccec5a434c2b9b6f12fbf7fd9.implementation
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.configureFirebaseConsole() {

    val libs = project.extensions.libs
    dependencies {
        implementation(platform(libs.findLibrary("google-firebase-bom").get()))
        implementation(libs.findLibrary("google-firebase-cloud-message").get())
        implementation(libs.findLibrary("google-firebase-functions").get())
    }

}