package ir.amirroid.clipshare.process.utils

import ir.amirroid.clipshare.process.wrapper.Base64Wrapper
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ByteArrayAsBase64Serializer : KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ByteArrayAsBase64", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ByteArray) {
        val base64 = Base64Wrapper.encodeToString(value)
        encoder.encodeString(base64)
    }

    override fun deserialize(decoder: Decoder): ByteArray {
        return Base64Wrapper.decodeToByteArray(decoder.decodeString())
    }
}