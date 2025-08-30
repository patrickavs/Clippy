plugins {
    alias(libs.plugins.local.android.application)
    alias(libs.plugins.local.compose.multiplatform)
    alias(libs.plugins.local.koin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.uiModels)
            implementation(libs.coil.compose)
            implementation(libs.adaptive)

            implementation(libs.richeditor.compose)
        }
    }
}