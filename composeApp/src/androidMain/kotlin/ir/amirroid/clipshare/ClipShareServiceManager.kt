package ir.amirroid.clipshare

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.edit

object ClipShareServiceManager {
    private val lock = Any()

    fun startServiceIfNotStarted(context: Context) {
        synchronized(lock) {
//            if (ServiceState.isStarted(context)) return
            Log.d("TEST", "Starting service")

            val intent = Intent(context, ClipboardForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }

            ServiceState.setStarted(context)
        }
    }
}

object ServiceState {
    private const val PREFS = "clipboard_service_prefs"
    private const val KEY_STARTED = "service_started"

    fun isStarted(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_STARTED, false)
    }

    fun setStarted(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit {
                putBoolean(KEY_STARTED, true)
            }
    }

    fun setStopped(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit {
                putBoolean(KEY_STARTED, false)
            }
    }
}