package ir.amirroid.clipshare.clipboard.models

import kotlinx.serialization.Serializable

@Serializable
data class HtmlWithPlainText(
    val text: String,
    val html: String
)
