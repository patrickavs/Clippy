package ir.amirroid.clipshare.storage

interface PlatformStorage {
    suspend fun saveToCache(bytes: ByteArray, postfix: String): String
    suspend fun saveToCacheWithFileName(bytes: ByteArray, fileName: String): String
    suspend fun deleteFile(path: String)
    suspend fun deleteAllCacheFiles()
    fun getFileInfo(path: String): FileInfo
    suspend fun readBytes(path: String): ByteArray
}
