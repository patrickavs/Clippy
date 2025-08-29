package ir.amirroid.clipshare.common.app.utils

import java.io.File

object CacheFiles {
    private const val CACHE_FOLDER_NAME = ".clip-share"

    val appCacheFolder by lazy {
        File(System.getProperty("user.home"), CACHE_FOLDER_NAME).also {
            if (it.exists().not()) it.mkdirs()
        }
    }
    val appFilesFolder by lazy {
        File(appCacheFolder, "files").also {
            if (it.exists().not()) it.mkdirs()
        }
    }
}