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
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "oneDayOneShot"
include(":app")
include(":core")
include(":core:commonsystem")
include(":feature")
include(":feature:intro")
include(":feature:user")
include(":core:network")
include(":core:data")
include(":core:datastore")
include(":core:room")
