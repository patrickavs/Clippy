plugins {
    alias(libs.plugins.local.android.library)
    alias(libs.plugins.local.kotlin)
    alias(libs.plugins.local.koin)
    alias(libs.plugins.sqldelight)
}
// For generate interfaces: generateCommonMainDatabaseInterface

kotlin {
    sourceSets.androidMain.dependencies {
        implementation(libs.sqldelight.driver.android)
    }
    sourceSets.commonMain.dependencies {
        implementation(libs.sqldelight.runtime)
        implementation(libs.sqldelight.coroutines)
    }
    sourceSets.nativeMain.dependencies {
        implementation(libs.sqldelight.driver.native)
    }
    sourceSets.desktopMain.dependencies {
        implementation(libs.sqlite.driver)
    }
}

val namespace = "ir.amirroid.clipshare.database"

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set(namespace)

            schemaOutputDirectory.set(
                file("src/commonMain/sqldelight/${namespace.replace('.', '/')}")
            )
        }
    }
}