pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven("https://jitpack.io")
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "oneDayOneShot"
include(":app")
include(":core")
include(":core:commonsystem")
include(":core:permission")
include(":feature")
include(":feature:intro")
include(":feature:user")
include(":core:network")
include(":core:data")
include(":core:datastore")
include(":core:database")
include(":core:model")
include(":feature:home")
include(":feature:editor")
include(":feature:diary")
include(":feature:calendar")
include(":feature:bookcase")
include(":feature:setting")
