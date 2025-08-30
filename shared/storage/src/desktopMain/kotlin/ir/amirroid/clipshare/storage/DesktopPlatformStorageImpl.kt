package ir.amirroid.clipshare.storage

import ir.amirroid.clipshare.common.app.utils.CacheFiles
import java.io.File

class DesktopPlatformStorageImpl : PlatformStorage {
    override suspend fun saveToCache(bytes: ByteArray, postfix: String): String {
        val file = CacheFiles.appFilesFolder.resolve("${System.currentTimeMillis()}.$postfix")
        file.writeBytes(bytes)
        return file.path
    }

    override suspend fun deleteFile(path: String) {
        File(path).delete()
    }

    override suspend fun deleteAllCacheFiles() {
        CacheFiles.appFilesFolder.listFiles()?.forEach { it.delete() }
    }
}