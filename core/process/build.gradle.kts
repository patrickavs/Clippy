plugins {
    alias(libs.plugins.local.android.library)
    alias(libs.plugins.local.kotlin)
    alias(libs.plugins.local.koin)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.clipboard)
            implementation(projects.core.database)
        }
    }
}