package ir.amirroid.clipshare.process.chunk

import ir.amirroid.clipshare.process.models.FileBufferChunked

interface FileChunkManager {
    suspend fun chunkFile(
        path: String,
        maxChunkSizeKb: Int,
        group: String? = null
    ): List<FileBufferChunked>

    suspend fun handleIncomingChunk(fileBuffer: FileBufferChunked): String?
}