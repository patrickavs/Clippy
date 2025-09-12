package ir.amirroid.clipshare.connectivity.models

data class DataChannelBuffer(
    val data: ByteArray,
    val binary: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DataChannelBuffer

        if (binary != other.binary) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = binary.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
