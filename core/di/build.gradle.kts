plugins {
    alias(libs.plugins.local.android.library)
    alias(libs.plugins.local.kotlin)
    alias(libs.plugins.local.koin)
}


kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.connectivity)
            implementation(projects.shared.clipboard)
            implementation(projects.shared.storage)
            implementation(projects.core.database)
            implementation(projects.core.process)
            implementation(projects.core.data)
            implementation(projects.core.domain)


            implementation(projects.features.history)
            implementation(projects.features.devices)
        }
    }
}