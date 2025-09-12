package ir.amirroid.clipshare.process.models

import ir.amirroid.clipshare.process.utils.ByteArrayAsBase64Serializer
import kotlinx.serialization.Serializable

@Serializable
data class FileBuffer(
    val fileName: String,
    @Serializable(ByteArrayAsBase64Serializer::class)
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FileBuffer

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