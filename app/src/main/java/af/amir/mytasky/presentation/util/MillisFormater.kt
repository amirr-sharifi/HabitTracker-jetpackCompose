package af.amir.mytasky.presentation.util

import java.util.Locale
import java.util.concurrent.TimeUnit

fun formatMillisToUserReadable(millis: Long): String {
    val hour = TimeUnit.MILLISECONDS.toHours(millis)
    val minute = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return if (hour > 0L) String.format(
        Locale.getDefault(),
        "%02d:%02d:%02d",
        hour,
        minute,
        seconds
    ) else String.format(Locale.getDefault(), "%02d:%02d", minute, seconds)
}