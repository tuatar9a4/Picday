package com.devd.build_logic.app

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies


fun Project.configureNavigationBundle(){

    val libs = project.extensions.libs
    dependencies{
        "implementation"(libs.findBundle("androidx.navigation.bundle").get())
    }

}
