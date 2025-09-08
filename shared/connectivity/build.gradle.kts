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
            implementation("dev.onvoid.webrtc:webrtc-java:0.14.0:windows-x86_64")
            implementation("dev.onvoid.webrtc:webrtc-java:0.14.0:macos-x86_64")
            implementation("dev.onvoid.webrtc:webrtc-java:0.14.0:macos-aarch64")
            implementation("dev.onvoid.webrtc:webrtc-java:0.14.0:linux-x86_64")
            implementation("dev.onvoid.webrtc:webrtc-java:0.14.0:linux-aarch64")
            implementation("dev.onvoid.webrtc:webrtc-java:0.14.0:linux-aarch32")
        }
        commonMain.dependencies {
            implementation(projects.shared.network)
        }
    }
}