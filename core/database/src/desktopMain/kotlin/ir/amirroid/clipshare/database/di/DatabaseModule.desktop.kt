package ir.amirroid.clipshare.database.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import ir.amirroid.clipshare.common.app.utils.CacheFiles
import ir.amirroid.clipshare.common.app.utils.Constants
import ir.amirroid.clipshare.database.AppDatabase
import org.koin.core.module.Module

actual fun Module.configureDriver() {
    single<SqlDriver> {
        val dbName = "${Constants.DB_NAME}.db"
        val file = CacheFiles.appCacheFolder.resolve(dbName)

        val driver = JdbcSqliteDriver("jdbc:sqlite:${file.path}")

        if (!file.exists()) {
            AppDatabase.Schema.create(driver)
        }

        driver.execute(null, "PRAGMA foreign_keys=ON;", 0)
        driver
    }
}