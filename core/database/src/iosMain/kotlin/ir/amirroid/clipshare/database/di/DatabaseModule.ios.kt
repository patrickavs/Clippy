package ir.amirroid.clipshare.database.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import ir.amirroid.clipshare.common.app.utils.Constants
import ir.amirroid.clipshare.database.AppDatabase
import org.koin.core.module.Module

actual fun Module.configureDriver() {
    single<SqlDriver> {
        NativeSqliteDriver(
            schema = AppDatabase.Schema,
            name = Constants.DB_NAME
        ).apply {
            execute(null, "PRAGMA foreign_keys=ON;", 0)
        }
    }
}