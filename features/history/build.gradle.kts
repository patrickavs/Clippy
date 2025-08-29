plugins {
    alias(libs.plugins.local.android.application)
    alias(libs.plugins.local.compose.multiplatform)
    alias(libs.plugins.local.koin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.connectivity)
            implementation(projects.shared.clipboard)
        }
    }
}