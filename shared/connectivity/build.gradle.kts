plugins {
    alias(libs.plugins.local.android.library)
    alias(libs.plugins.local.kotlin)
    alias(libs.plugins.local.koin)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.webrtc.android)
        }
        desktopMain.dependencies {
            implementation(libs.webrtc.java)
        }
        commonMain.dependencies {
            implementation(projects.shared.network)
        }
    }
}