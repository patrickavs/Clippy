package ir.amirroid.clipshare.convention.android.library

import com.android.build.api.dsl.LibraryExtension
import ir.amirroid.clipshare.convention.PACKAGE_NAME
import ir.amirroid.clipshare.convention.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Project

internal fun Project.configureAndroidLibraryPlugins(
    extensions: LibraryExtension
) {
    fun String.versionInt(): Int =
        libs.findVersion("android-$this").get().requiredVersion.toInt()

    extensions.apply {
        namespace = PACKAGE_NAME
        compileSdk = "compileSdk".versionInt()

        defaultConfig {
            minSdk = "minSdk".versionInt()
        }

        packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        lint {
            disable.add("NullSafeMutableLiveData")
        }
    }
}
