package ir.amirroid.clipshare.database.di

import app.cash.sqldelight.db.SqlDriver
import ir.amirroid.clipshare.database.AppDatabase
import ir.amirroid.clipshare.database.dao.ClipboardDao
import ir.amirroid.clipshare.database.dao.ClipboardDaoImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect fun Module.configureDriver()

val databaseModule = module {
    configureDriver()
    single<AppDatabase> {
        AppDatabase(get<SqlDriver>())
    }
    single { get<AppDatabase>().clipboardQueries }
    singleOf(::ClipboardDaoImpl).bind<ClipboardDao>()
}