package ir.amirroid.clipshare.data.mapper

import ir.amirroid.clipshare.clipboard.models.HtmlWithPlainText
import ir.amirroid.clipshare.common.app.extensions.fromSeconds
import ir.amirroid.clipshare.database.entity.ClipboardEntity
import ir.amirroid.clipshare.database.entity.ClipboardType
import ir.amirroid.clipshare.domain.models.clipboard.ClipboardContentDomain
import kotlinx.serialization.json.Json

fun ClipboardEntity.toDomain(json: Json): ClipboardContentDomain {
    return when (type) {
        ClipboardType.RTF -> ClipboardContentDomain.RichText(
            content = data,
            type = ClipboardContentDomain.RichText.Type.RTF,
            id = id,
            createdAt = createdAt.fromSeconds()
        )

        ClipboardType.HTML -> ClipboardContentDomain.RichText(
            content = json.decodeFromString<HtmlWithPlainText>(data).html,
            type = ClipboardContentDomain.RichText.Type.HTML,
            id = id,
            createdAt = createdAt.fromSeconds()
        )

        ClipboardType.IMAGE -> ClipboardContentDomain.Image(
            path = data, id = id,
            createdAt = createdAt.fromSeconds()
        )

        ClipboardType.TEXT -> ClipboardContentDomain.Text(
            value = data, id = id,
            createdAt = createdAt.fromSeconds()
        )

        ClipboardType.FILES -> ClipboardContentDomain.Files(
            paths = json.decodeFromString(data),
            id = id,
            createdAt = createdAt.fromSeconds()
        )
    }
}