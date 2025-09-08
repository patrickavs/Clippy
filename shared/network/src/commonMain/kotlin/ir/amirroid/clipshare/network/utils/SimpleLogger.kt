package ir.amirroid.clipshare.network.utils

import co.touchlab.kermit.Logger
import io.ktor.client.plugins.logging.Logger as KtorLogger


class SimpleLogger : KtorLogger {
    override fun log(message: String) {
        Logger.withTag("NetworkLogger").d { message }
    }
}