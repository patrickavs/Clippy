package ir.amirroid.clipshare.process.wrapper

import android.util.Base64

actual object Base64Wrapper {
    actual fun encodeToString(bytes: ByteArray): String =
        Base64.encodeToString(bytes, Base64.NO_WRAP)

    actual fun decodeToByteArray(value: String): ByteArray =
        Base64.decode(value, Base64.DEFAULT)
}