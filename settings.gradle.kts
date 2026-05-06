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
include(":shared:common:compose")
include(":shared:connectivity")
include(":shared:clipboard")
include(":shared:network")
include(":shared:storage")

// Core
include(":core:di")
include(":core:navigation")
include(":core:database")
include(":core:process")
include(":core:data")
include(":core:domain")
include(":core:resources")
include(":core:ui-models")
include(":core:design-system")

// Features
include(":features:history")
include(":features:devices")
include(":features:scanner")
include(":features:qrcode")

// Server
include(":signaling-server")

includeBuild("build-logic")
