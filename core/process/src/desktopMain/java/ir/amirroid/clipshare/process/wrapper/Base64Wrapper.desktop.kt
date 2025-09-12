package ir.amirroid.clipshare.process.wrapper

import java.util.Base64

actual object Base64Wrapper {
    actual fun encodeToString(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }

    actual fun decodeToByteArray(value: String): ByteArray {
        return Base64.getDecoder().decode(value)
    }
}