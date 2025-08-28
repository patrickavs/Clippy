import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.local.android.application)
    alias(libs.plugins.local.compose.multiplatform)
    alias(libs.plugins.local.koin)
    alias(libs.plugins.composeHotReload)
}

compose.desktop {
    application {
        mainClass = "ir.amirroid.clipshare.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ir.amirroid.clipshare"
            packageVersion = rootProject.version.toString()
        }
    }
}
