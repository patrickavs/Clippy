plugins {
    alias(libs.plugins.local.android.library)
    alias(libs.plugins.local.kotlin)
    alias(libs.plugins.local.compose.multiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.navigation.compose)

            implementation(projects.features.history)
            implementation(projects.features.devices)
        }
    }
}