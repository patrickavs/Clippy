package ir.amirroid.clipshare.process.storage

import ir.amirroid.clipshare.common.app.utils.CacheFiles

class DesktopPlatformStorageImpl : PlatformStorage {
    override suspend fun saveToCache(bytes: ByteArray, postfix: String): String {
        val file = CacheFiles.appFilesFolder.resolve("${System.currentTimeMillis()}.$postfix")
        file.writeBytes(bytes)
        return file.path
    }
}