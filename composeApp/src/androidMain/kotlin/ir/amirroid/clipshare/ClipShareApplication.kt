package ir.amirroid.clipshare

import android.app.Application
import ir.amirroid.clipshare.di.DependencyInjection
import org.koin.android.ext.koin.androidContext

class ClipShareApplication : Application() {
    override fun onCreate() {
        DependencyInjection.configure { androidContext(this@ClipShareApplication) }
        super.onCreate()
    }
}