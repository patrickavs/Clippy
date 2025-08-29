rootProject.name = "ClipShare"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        maven("https://en-mirror.ir")
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        maven("https://en-mirror.ir")
        mavenCentral()
    }
}


include(":composeApp")

// Shared
include(":shared:common:app")
include(":shared:connectivity")
include(":shared:clipboard")

// Core
include(":core:di")
include(":core:navigation")
include(":core:database")
include(":core:process")
include(":core:data")
include(":core:domain")
include(":core:ui-models")
include(":core:design-system")

// Features
include(":features:history")

includeBuild("build-logic")