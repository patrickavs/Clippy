package ir.amirroid.clipshare.convention.compose

import ir.amirroid.clipshare.convention.androidMain
import ir.amirroid.clipshare.convention.commonMain
import ir.amirroid.clipshare.convention.composeDependencies
import ir.amirroid.clipshare.convention.desktopMain
import ir.amirroid.clipshare.convention.libs
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

internal fun Project.configureComposeMultiplatformPlugins(
    extensions: KotlinMultiplatformExtension
) {
    extensions.apply {
        configureCommonMain(sourceSets)
        configureAndroidMain(sourceSets)
        configureDesktopMain(sourceSets)
    }
}


private fun Project.configureCommonMain(sourceSets: NamedDomainObjectContainer<KotlinSourceSet>) {
    val commonMain = sourceSets.commonMain
    val dependencies = composeDependencies
    commonMain.dependencies {
        implementation(dependencies.runtime)
        implementation(dependencies.foundation)
        implementation(dependencies.ui)
        implementation(dependencies.material3)
        implementation(dependencies.components.resources)

        implementation(libs.findLibrary("androidx-lifecycle-viewmodel").get())
        implementation(libs.findLibrary("androidx-lifecycle-runtimeCompose").get())
    }
}

private fun Project.configureAndroidMain(sourceSets: NamedDomainObjectContainer<KotlinSourceSet>) {
    val androidMain = sourceSets.androidMain
    androidMain.dependencies {
        implementation(composeDependencies.preview)
        implementation(libs.findLibrary("androidx-activity-compose").get())
    }
}

private fun Project.configureDesktopMain(sourceSets: NamedDomainObjectContainer<KotlinSourceSet>) {
    val desktopMain = sourceSets.desktopMain
    desktopMain.dependencies {
        implementation(composeDependencies.desktop.currentOs)
        implementation(libs.findLibrary("kotlinx-coroutinesSwing").get())
    }
}