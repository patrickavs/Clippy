package ir.amirroid.clipshare.convention.kotlin

import ir.amirroid.clipshare.convention.androidMain
import ir.amirroid.clipshare.convention.commonMain
import ir.amirroid.clipshare.convention.implementIfNotSelf
import ir.amirroid.clipshare.convention.libs
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

@OptIn(ExperimentalWasmDsl::class)
internal fun Project.configureKotlinMultiplatformPlugins(extensions: KotlinMultiplatformExtension) {
    extensions.apply {
        applyDefaultHierarchyTemplate()
        androidTarget()
        configureIosTargets()
        jvm("desktop")

        configureCommonMain(sourceSets)
        configureAndroidMain(sourceSets)
    }
}

private fun KotlinMultiplatformExtension.configureIosTargets() {
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
}

private fun Project.configureCommonMain(sourceSets: NamedDomainObjectContainer<KotlinSourceSet>) {
    sourceSets.commonMain.dependencies {
        implementation(libs.findLibrary("kotlinx-serialization").get())
        implementation(libs.findLibrary("kermit").get())
        implementation(libs.findLibrary("kotlinx-collections").get())
    }
}

private fun Project.configureAndroidMain(sourceSets: NamedDomainObjectContainer<KotlinSourceSet>) {
    sourceSets.androidMain.dependencies {
        implementation(libs.findLibrary("androidx-core-ktx").get())
    }
}