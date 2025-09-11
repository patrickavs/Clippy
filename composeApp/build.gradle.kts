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
    }
}

compose.desktop {
    application {
        mainClass = "ir.amirroid.clipshare.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ir.amirroid.clipshare"
            packageVersion = "1.0.0"

            jvmArgs("-Dapple.awt.application.appearance=system")
        }
    }
}
