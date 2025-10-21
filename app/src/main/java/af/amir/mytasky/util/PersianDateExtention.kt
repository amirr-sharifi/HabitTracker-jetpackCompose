package af.amir.mytasky.util

import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import saman.zamani.persiandate.PersianDate
import java.util.Locale

fun PersianDate.toEpochDay(): Long {
    val daysInYear : Long =
        ((this.shYear - 1) * 365 + ((this.shYear - 1) / 4)).toLong() // سال‌های قبل
    val daysInMonth = this.shMonth.let { month ->
        when (month) {
            1 -> 0
            2 -> 31
            3 -> 62
            4 -> 93
            5 -> 124
            6 -> 155
            7 -> 186
            8 -> 216
            9 -> 246
            10 -> 276
            11 -> 306
            12 -> 336
            else -> 0
        }
    }
    return (daysInYear + daysInMonth + this.shDay)
}


fun PersianDate.isDayBefore(persianDate: PersianDate): Boolean {
    return this.toEpochDay() < persianDate.toEpochDay()
}

fun PersianDate.toStringFormat(): String {
    val date = listOf(this.shYear, this.shMonth, this.shDay)
    return date.joinToString("-")
}

fun String.toPersianDate(): PersianDate {
    val date = this.split("-")
    val persianDate = PersianDate().apply {
        shYear = date[0].toInt()
        shMonth = date[1].toInt()
        shDay = date[2].toInt()
    }
    return persianDate
}



fun PersianDate.getMonthNameForDevice():String{
    return if (isDeviceLanguagePersian()) monthName else FinglishMonthName()
}
fun PersianDate.getDayNameForDevice():String{
    return if (isDeviceLanguagePersian()) dayName() else dayFinglishName()
}

private fun isDeviceLanguagePersian():Boolean{
    return Locale.getDefault().language == "fa"
}

fun PersianDate.toLocalDate(zone : ZoneId = ZoneId.systemDefault()) : LocalDate{
    val timeInMillis = this.toDate().time
    val localDate = Instant.ofEpochMilli(timeInMillis)
        .atZone(zone)
        .toLocalDate()
    return localDate
}
