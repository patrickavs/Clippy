package ir.amirroid.clipshare.database.mapper

import ir.amirroid.clipshare.database.ClipboardEntries
import ir.amirroid.clipshare.database.entity.ClipboardEntity
import ir.amirroid.clipshare.database.entity.ClipboardType

fun ClipboardEntries.toEntity() = ClipboardEntity(
    id = id,
    data = data_,
    type = ClipboardType.valueOf(type),
    createdAt = createdAt
)