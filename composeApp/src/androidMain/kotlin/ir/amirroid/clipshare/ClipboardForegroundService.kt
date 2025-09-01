package ir.amirroid.clipshare

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ir.amirroid.clipshare.process.service.ClipboardProcessorService
import org.koin.android.ext.android.inject

class ClipboardForegroundService : Service() {
    private val clipboardProcess: ClipboardProcessorService by inject()

    override fun onCreate() {
        clipboardProcess.start()
        startForegroundService()
        super.onCreate()
    }

    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Clipboard Service Active")
            .setContentText("Clipboard is being monitored")
            .setSmallIcon(R.drawable.ic_copy)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        clipboardProcess.dispose()
        ServiceState.setStopped(this)
    }
}