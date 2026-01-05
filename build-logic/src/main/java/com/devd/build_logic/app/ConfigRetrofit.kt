package com.devd.build_logic.app

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.configNetworkRetrofit(){

    
    val libs = project.extensions.libs

    dependencies{
        "implementation"(libs.findBundle("squareup.retrofit.bundle").get())
    }
}

class AndroidRetrofitPlugin : Plugin<Project>{
    override fun apply(target: Project) {
        with(target){
            configNetworkRetrofit()
        }
    }
}