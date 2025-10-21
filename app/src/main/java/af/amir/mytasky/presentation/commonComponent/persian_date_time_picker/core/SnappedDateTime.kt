package af.amir.mytasky.presentation.commonComponent.persian_date_time_picker.core

import org.threeten.bp.LocalTime
import saman.zamani.persiandate.PersianDate

internal sealed class SnappedDate(val snappedPersianDate: PersianDate, val snappedIndex: Int) {
    data class DayOfMonth (val localDate: PersianDate, val index: Int): SnappedDate(localDate, index)
    data class Month(val localDate: PersianDate, val index: Int): SnappedDate(localDate, index)
    data class Year (val localDate: PersianDate, val index: Int): SnappedDate(localDate, index)
}

internal sealed class SnappedTime(val snappedLocalTime: LocalTime, val snappedIndex: Int) {
    data class Hour (val localTime: LocalTime, val index: Int): SnappedTime(localTime, index)
    data class Minute (val localTime: LocalTime, val index: Int): SnappedTime(localTime, index)
}

