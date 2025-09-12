package ir.amirroid.clipshare.database.entity

data class ClipboardEntity(
    val id: Long,
    val data: String,
    val type: ClipboardType,
    val createdAt: Long,
)

enum class ClipboardType {
    TEXT, FILES, IMAGE, HTML, RTF
}