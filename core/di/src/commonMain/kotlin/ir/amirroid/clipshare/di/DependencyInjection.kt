package ir.amirroid.clipshare.di

import ir.amirroid.clipshare.clipboard.di.clipboardModule
import ir.amirroid.clipshare.connectivity.di.connectivityModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

object DependencyInjection {
    fun configure(appDeclaration: KoinAppDeclaration? = null) = startKoin {
        appDeclaration?.invoke(this)
        modules(
            otherModules, connectivityModule, clipboardModule
        )
    }
}