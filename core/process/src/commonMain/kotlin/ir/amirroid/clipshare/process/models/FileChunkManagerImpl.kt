package ir.amirroid.clipshare.process.models


import ir.amirroid.clipshare.process.chunk.FileChunkManager
import ir.amirroid.clipshare.storage.PlatformStorage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FileChunkManagerImpl(
    private val storage: PlatformStorage
) : FileChunkManager {

    private val receivedChunks = mutableMapOf<String, MutableList<FileBufferChunked>>()
    private val mutex = Mutex()

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun chunkFile(
        path: String,
        maxChunkSizeKb: Int,
        group: String?
    ): List<FileBufferChunked> {
        val info = storage.getFileInfo(path)
        if (!info.exists || info.isDirectory) return emptyList()

        val bytes = storage.readBytes(path)
        val maxChunkSize = maxChunkSizeKb * 1024
        val totalChunks = calculateTotalChunks(bytes.size, maxChunkSize)
        val fileId = Uuid.random().toString()

        return (0 until totalChunks).map { index ->
            val range = calculateChunkRange(index, maxChunkSize, bytes.size)
            val chunk = bytes.copyOfRange(range.first, range.second)

            FileBufferChunked(
                fileId = fileId,
                fileName = info.name,
                index = index,
                total = totalChunks,
                data = chunk,
                group = group
            )
        }
    }

    override suspend fun handleIncomingChunk(fileBuffer: FileBufferChunked): String? {
        return mutex.withLock {
            val chunks = receivedChunks.getOrPut(fileBuffer.fileId) { mutableListOf() }
            chunks.add(fileBuffer)

            if (chunks.size == fileBuffer.total) {
                val savedPath = assembleFile(chunks, fileBuffer.fileName, fileBuffer.fileId)
                receivedChunks.remove(fileBuffer.fileId)
                savedPath
            } else null
        }
    }

    private fun calculateTotalChunks(fileSize: Int, chunkSize: Int): Int {
        return (fileSize + chunkSize - 1) / chunkSize
    }

    private fun calculateChunkRange(index: Int, chunkSize: Int, totalSize: Int): Pair<Int, Int> {
        val start = index * chunkSize
        val end = minOf(start + chunkSize, totalSize)
        return start to end
    }

    private suspend fun assembleFile(
        chunks: List<FileBufferChunked>,
        fileName: String,
        fileId: String
    ): String {
        val sorted = chunks.sortedBy { it.index }
        val allBytes = sorted.flatMap { it.data.asIterable() }.toByteArray()
        return storage.saveToCacheWithFileName(allBytes, fileName.ifBlank { fileId })
    }
}