package ir.amirroid.clipshare.di

import ir.amirroid.clipshare.clipboard.di.clipboardModule
import ir.amirroid.clipshare.connectivity.di.connectivityModule
import ir.amirroid.clipshare.data.di.repositoryModule
import ir.amirroid.clipshare.database.di.databaseModule
import ir.amirroid.clipshare.domain.di.useCaseModule
import ir.amirroid.clipshare.process.di.processorModule
import ir.amirroid.clipshare.storage.storageModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

object DependencyInjection {
    fun configure(appDeclaration: KoinAppDeclaration? = null) = startKoin {
        appDeclaration?.invoke(this)
        modules(
            otherModules, connectivityModule, clipboardModule, databaseModule, processorModule,
            repositoryModule, useCaseModule, viewModelModule, storageModule
        )
    }
}