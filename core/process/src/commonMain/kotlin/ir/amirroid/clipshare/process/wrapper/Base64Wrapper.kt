package ir.amirroid.clipshare.process.wrapper

expect object Base64Wrapper {
    fun encodeToString(bytes: ByteArray): String
    fun decodeToByteArray(value: String): ByteArray
}