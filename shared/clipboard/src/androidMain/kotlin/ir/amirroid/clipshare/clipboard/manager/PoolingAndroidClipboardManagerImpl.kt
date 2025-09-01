package ir.amirroid.clipshare.clipboard.manager

import android.content.Context
import ir.amirroid.clipshare.clipboard.models.ClipboardContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * We use a polling mechanism here instead of onPrimaryClipChangedListener because:
 *
 * - The system listener only triggers when the app is in focus or when our keyboard
 *   is set as the default input method.
 * - Our goal is to monitor clipboard changes continuously, even when the app is in the
 *   background and the default keyboard is not our own.
 *
 * Therefore, we periodically check the clipboard content in a coroutine loop and
 * trigger the listener whenever a change is detected.
 */
class PoolingAndroidClipboardManagerImpl(
    context: Context,
    json: Json
) : BasicClipboardManager(context, json) {
    private var lastContent: ClipboardContent? = null

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun addOnChangedListener(action: (ClipboardContent) -> Unit) {
        job?.cancel()

        job = scope.launch {
            lastContent = getClipboardContent()
            while (isActive) {
                val content = getClipboardContent()
                if (content != null && content != lastContent) {
                    lastContent = content
                    action(content)
                }
                delay(POOLING_DELAY)
            }
        }
    }

    override fun dispose() {
        job?.cancel()
        job = null
    }

    companion object {
        private const val POOLING_DELAY = 200L
    }
}