package ir.amirroid.clipshare.process.connection

interface ConnectionManager {
    suspend fun start()
    fun close()
}