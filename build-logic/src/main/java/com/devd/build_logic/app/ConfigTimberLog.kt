package com.devd.build_logic.app

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies


fun Project.configTimberLogger(){

    dependencies{
        val libs = project.extensions.libs
        "implementation"(libs.findLibrary("logger.timber").get())
    }

}