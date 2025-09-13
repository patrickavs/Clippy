package ir.amirroid.clipshare.process.models

import ir.amirroid.clipshare.process.utils.ByteArrayAsBase64Serializer
import kotlinx.serialization.Serializable

@Serializable
data class FileBufferChunked(
    val fileName: String,
    val index: Int,
    val total: Int,
    @Serializable(ByteArrayAsBase64Serializer::class)
    val data: ByteArray,
    val fileId: String,
    val group: String?,
    val isLastItemInGroup: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FileBufferChunked

        if (fileName != other.fileName) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}