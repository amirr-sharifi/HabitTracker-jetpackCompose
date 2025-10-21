package af.amir.mytasky.presentation.edit_habit

import af.amir.mytasky.R
import androidx.annotation.StringRes
import org.threeten.bp.LocalTime
import saman.zamani.persiandate.PersianDate

data class EditHabitState(
    val isLoading : Boolean = false,
    val updateMode: Boolean = false,

    val title: String = "",
    val description: String = "",

    val habitType: UIHabitType = UIHabitType.Simple,
    val habitCount: Int = 2,
    val habitTimedHour: Int = 0,
    val habitTimedMinute: Int = 1,

    val habitFrequency: UIHabitFrequency = UIHabitFrequency.Daily,
    val habitDailyExceptionDays: List<PersianDate> = emptyList(),
    val habitWeeklyDays: List<Int> = emptyList(),
    val habitMonthlyDays: List<Int> = emptyList(),
    val habitIntervalXNumber: Int= 1,
    val habitIntervalStartDay: PersianDate =  PersianDate.today(),

    val habitRepetition: UIHabitRepetition = UIHabitRepetition.Once,
    val onceTimeHabitHour: Int = 0,
    val onceTimeHabitMinute: Int = 0,
    val multipleHabitTimes: List<LocalTime> = emptyList(),
    val reminderEnabled: Boolean = false,

)

enum class UIHabitType(
    @StringRes
    val stringResId: Int
) {
    Simple(R.string.simple), Countable(R.string.countable), Timed(R.string.timed)
}

enum class UIHabitFrequency(
    @StringRes
    val stringResId: Int,
) {
    Daily(R.string.daily), Weekly(R.string.weekly), Monthly(R.string.monthly), Interval(R.string.everyxday)
}

enum class UIHabitRepetition(
    @StringRes
    val stringResId: Int
) {
    Once(R.string.once), Multiple(R.string.multiple)
}
