import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.local.android.application)
    alias(libs.plugins.local.compose.multiplatform)
    alias(libs.plugins.local.koin)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Core
            implementation(projects.core.di)
            implementation(projects.core.navigation)
            implementation(projects.core.process)
            implementation(projects.core.uiModels)
            implementation(projects.core.domain)

            // Shared
            implementation(projects.shared.connectivity)
            implementation(projects.shared.clipboard)
        }

        desktopMain.dependencies {
            implementation(libs.auto.launch)
        }
    }
}

compose.desktop {
    application {
        mainClass = "ir.amirroid.clipshare.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ClipShare"
            packageVersion = "1.0.0"

            macOS {
                iconFile = project.file("icons").resolve("clipshare.icns")
            }

            windows {
                iconFile = project.file("icons").resolve("clipshare.jpeg")
            }

            linux {
                iconFile = project.file("icons").resolve("clipshare.jpeg")
            }


            nativeDistributions {
                modules("java.sql")
            }

            jvmArgs("-Dapple.awt.application.appearance=system")
        }
    }
}
