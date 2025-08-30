package ir.amirroid.clipshare.common.compose

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import kotlinx.datetime.*
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.times

@Composable
fun RelativeTimeText(
    time: LocalDateTime,
    style: TextStyle = LocalTextStyle.current,
    modifier: Modifier = Modifier
) {
    var duration by remember { mutableStateOf(timeSince(time)) }

    LaunchedEffect(time) {
        while (true) {
            duration = timeSince(time)

            // dynamic interval: 1s if < 1 minute, otherwise 5s
            val interval = if (duration < 1.minutes) 1_000L else 5_000L
            delay(interval)
        }
    }

    val text = remember(time, duration) {
        formatRelativeTime(time, duration)
    }

    Text(
        text = text,
        style = style,
        textAlign = TextAlign.Start,
        modifier = modifier
    )
}

@OptIn(ExperimentalTime::class)
private fun timeSince(time: LocalDateTime): Duration {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return now.toInstant(TimeZone.currentSystemDefault())
        .minus(time.toInstant(TimeZone.currentSystemDefault()))
}

@OptIn(FormatStringsInDatetimeFormats::class)
private fun formatRelativeTime(
    time: LocalDateTime,
    duration: Duration
): String {
    return when {
        duration < 60.seconds -> "${duration.inWholeSeconds} seconds ago"
        duration < 60.minutes -> "${duration.inWholeMinutes} minutes ago"
        duration < 24 * 60.minutes -> "${duration.inWholeHours} hours ago"
        duration < 30 * 24 * 60.minutes -> "${duration.inWholeDays} days ago"
        else -> {
            time.format(
                LocalDateTime.Format { byUnicodePattern("yyyy/MM/dd") }
            )
        }
    }
}