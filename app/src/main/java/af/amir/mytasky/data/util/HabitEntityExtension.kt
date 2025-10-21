package af.amir.mytasky.data.util

import af.amir.mytasky.data.local.db.entity.HabitEntity
import af.amir.mytasky.data.mapper.toHabitFrequency
import af.amir.mytasky.domain.model.HabitDate
import af.amir.mytasky.domain.model.HabitFrequency
import af.amir.mytasky.util.isDayBefore
import af.amir.mytasky.util.toLocalDate
import af.amir.mytasky.util.toPersianDate
import android.util.Log

fun HabitEntity.isRelevantToDay(date : String): Boolean{
    val currentDate = date.toPersianDate()
    val _frequency = frequency.toHabitFrequency()
    Log.e("DayHabit", "isRelevantToDay: $date", )
    if (currentDate.isDayBefore(createdAt.toPersianDate()))
        return false

    return when(_frequency){
        is HabitFrequency.Daily -> HabitDate(date) !in _frequency.exceptionDays
        is HabitFrequency.Weekly -> {
            val daysOrdinals = _frequency.daysInWeeks
            val todayOrdinal = currentDate.toLocalDate().dayOfWeek.ordinal
            todayOrdinal in daysOrdinals
        }
        is HabitFrequency.Monthly -> {
            val currentIndex = currentDate.shDay -1
            currentIndex in _frequency.daysInMonth
        }
        is HabitFrequency.Interval -> {
            val startDate = _frequency.echoDay.date.toPersianDate()
            val diff = currentDate.dayInYear - startDate.dayInYear
            return diff % _frequency.everyXDay.toLong() == 0L
        }
    }
}