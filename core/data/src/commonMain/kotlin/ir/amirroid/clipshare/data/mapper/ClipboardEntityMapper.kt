package ir.amirroid.clipshare.data.mapper

import ir.amirroid.clipshare.database.entity.ClipboardEntity
import ir.amirroid.clipshare.database.entity.ClipboardType
import ir.amirroid.clipshare.domain.models.ClipboardContentDomain
import kotlinx.serialization.json.Json

fun ClipboardEntity.toDomain(json: Json): ClipboardContentDomain {
    return when (type) {
        ClipboardType.RTF -> ClipboardContentDomain.RichText(
            content = data,
            type = ClipboardContentDomain.RichText.Type.RTF,
            id = id
        )

        ClipboardType.HTML -> ClipboardContentDomain.RichText(
            content = data,
            type = ClipboardContentDomain.RichText.Type.HTML,
            id = id
        )

        ClipboardType.IMAGE -> ClipboardContentDomain.Image(path = data, id = id)
        ClipboardType.TEXT -> ClipboardContentDomain.Text(value = data, id = id)
        ClipboardType.FILES -> ClipboardContentDomain.Files(
            paths = json.decodeFromString(data),
            id = id
        )
    }
}