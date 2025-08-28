package ir.amirroid.clipshare.common.app.utils

import java.io.File

object CacheFiles {
    val appCacheFolder by lazy {
        File(System.getProperty("user.home"), Constants.APP_NAME).also {
            if (it.exists().not()) it.mkdirs()
        }
    }
}