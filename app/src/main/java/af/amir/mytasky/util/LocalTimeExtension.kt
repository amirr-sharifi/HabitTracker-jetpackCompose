package af.amir.mytasky.util

import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter


fun LocalTime.toStringFormat(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return this.format(formatter)
}


fun String.toLocalTime(): LocalTime {
    val time = LocalTime.parse(this)
    return time
}



