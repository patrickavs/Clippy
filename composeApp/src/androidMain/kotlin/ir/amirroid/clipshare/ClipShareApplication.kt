package ir.amirroid.clipshare

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import ir.amirroid.clipshare.di.DependencyInjection
import org.koin.android.ext.koin.androidContext

class ClipShareApplication : Application() {
    override fun onCreate() {
        DependencyInjection.configure {
            androidContext(this@ClipShareApplication)
            modules(appModule)
        }
        createNotificationChannel()
        super.onCreate()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                CHANNEL_ID,
                "Clipboard Foreground Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(chan)
        }
    }
}