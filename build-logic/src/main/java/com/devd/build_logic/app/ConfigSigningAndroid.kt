package com.devd.build_logic.app

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import java.io.File

fun Project.configureSigningAndroid(){

    androidExtension.apply {
        signingConfigs{
            create("release"){
                val config = rootProject.extra["signingConfigs"] as Map<*,*>
                val releaseConfig = config["releaseConfig"] as Map<*,*>

                keyAlias = releaseConfig["keyAlias"] as String
                keyPassword  = releaseConfig["keyPassword"] as String
                storeFile = releaseConfig["storeFile"] as File
                storePassword = releaseConfig["storePassword"] as String
            }
            create("debugSigning"){
                val config = rootProject.extra["signingConfigs"] as Map<*,*>
                val releaseConfig = config["debugConfig"] as Map<*,*>

                keyAlias = releaseConfig["keyAlias"] as String
                keyPassword  = releaseConfig["keyPassword"] as String
                storeFile = releaseConfig["storeFile"] as File
                storePassword = releaseConfig["storePassword"] as String
            }
        }


    }

}