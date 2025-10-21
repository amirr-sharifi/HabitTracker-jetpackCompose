package af.amir.mytasky.presentation.edit_habit

import android.app.Activity
import org.threeten.bp.LocalTime
import saman.zamani.persiandate.PersianDate

sealed interface EditHabitEvent {
    data class OnTitleChange(val title: String) : EditHabitEvent
    data class OnDescriptionChange(val description: String) : EditHabitEvent

    data object OnSaveHabit : EditHabitEvent
    data object OnDeleteHabit : EditHabitEvent

    data class OnHabitTypeChange(val type: UIHabitType) : EditHabitEvent
    data object OnIncreaseCount : EditHabitEvent
    data object OnDecreaseCount : EditHabitEvent
    data class OnHourTimedHabitChange(val hour: Int) : EditHabitEvent
    data class OnMinuteTimedHabitChange(val minute: Int) : EditHabitEvent

    data class OnHabitFrequencyChange(val frequency: UIHabitFrequency) : EditHabitEvent
    data class OnAddDailyHabitExceptionDay(val day: PersianDate) : EditHabitEvent
    data class OnRemoveDailyHabitExceptionDay(val index: Int) : EditHabitEvent
    data class OnToggleDayOfWeeklyHabit(val index: Int) : EditHabitEvent
    data class OnToggleDayOfMonthlyHabit(val index: Int) : EditHabitEvent
    data class OnIntervalHabitStartDayChange(val day: PersianDate) : EditHabitEvent
    data class OnIntervalHabitValueChange(val value: Int) : EditHabitEvent

    data class OnHabitRepetitionInDayChange(val repetition: UIHabitRepetition) : EditHabitEvent
    data class OnHourRepetitionInDayChange(val hour: Int) : EditHabitEvent
    data class OnMinuteRepetitionInDayChange(val minute: Int) : EditHabitEvent
    data class OnAddRepetitionTime(val time: LocalTime) : EditHabitEvent
    data class OnRemoveRepetitionTime(val index: Int) : EditHabitEvent
    data class OnReminderEnableChange(val enable: Boolean) : EditHabitEvent

    data class OnNotificationPermissionGranted(val isGranted : Boolean) : EditHabitEvent

}

