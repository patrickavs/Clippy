package ir.amirroid.clipshare.process.storage

interface PlatformStorage {
    suspend fun saveToCache(bytes: ByteArray, postfix: String): String
}