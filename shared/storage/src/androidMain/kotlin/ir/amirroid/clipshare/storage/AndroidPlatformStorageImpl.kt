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

    override suspend fun saveToCacheWithFileName(
        bytes: ByteArray,
        fileName: String
    ): String {
        val file = context.filesDir.resolve(fileName)
        file.writeBytes(bytes)
        return file.path
    }

    override suspend fun deleteFile(path: String) {
        File(path).delete()
    }

    override suspend fun deleteAllCacheFiles() {
        context.filesDir.listFiles()?.forEach { it.delete() }
    }

    override fun getFileInfo(path: String): FileInfo {
        val file = File(path)
        return FileInfo(
            name = file.name,
            exists = file.exists(),
            isDirectory = file.isDirectory,
            length = file.length()
        )
    }

    override suspend fun readBytes(path: String): ByteArray {
        val file = File(path)
        return file.readBytes()
    }
}