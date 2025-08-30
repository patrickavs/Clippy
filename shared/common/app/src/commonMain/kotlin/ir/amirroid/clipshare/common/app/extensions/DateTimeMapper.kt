package ir.amirroid.clipshare.common.app.extensions

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun Long.fromMilliseconds() =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())

@OptIn(ExperimentalTime::class)
fun Long.fromSeconds() =
    Instant.fromEpochSeconds(this).toLocalDateTime(TimeZone.currentSystemDefault())