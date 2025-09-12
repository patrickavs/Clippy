package ir.amirroid.clipshare.storage

data class FileInfo(
    val name: String,
    val exists: Boolean,
    val isDirectory: Boolean,
    val length: Long
)
