package ir.amirroid.clipshare.storage

import android.content.Context
import java.io.File
import kotlin.collections.forEach

class AndroidPlatformStorageImpl(
    private val context: Context
) : PlatformStorage {
    override suspend fun saveToCache(bytes: ByteArray, postfix: String): String {
        val file = context.filesDir.resolve("${System.currentTimeMillis()}.$postfix")
        file.writeBytes(bytes)
        return file.path
    }

    override suspend fun deleteFile(path: String) {
        File(path).delete()
    }

    override suspend fun deleteAllCacheFiles() {
        context.filesDir.listFiles()?.forEach { it.delete() }
    }
}