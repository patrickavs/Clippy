package ir.amirroid.clipshare.database.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ir.amirroid.clipshare.common.app.utils.Constants
import ir.amirroid.clipshare.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module

actual fun Module.configureDriver() {
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = androidContext(),
            name = Constants.DB_NAME,
        )
    }
}