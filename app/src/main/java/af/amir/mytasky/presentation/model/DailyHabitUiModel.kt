package af.amir.mytasky.presentation.model

import af.amir.mytasky.domain.model.DailyHabitTimerStatus
import af.amir.mytasky.domain.model.HabitDate
import af.amir.mytasky.domain.model.HabitTime

sealed interface DailyHabitUiModel {
    val id: Long
    val date: HabitDate
    val title: String
    val description: String?
    val time: HabitTime

}

data class SimpleHabitUiModel(
    override val id: Long,
    override val date: HabitDate,
    override val title: String,
    override val description: String?,
    override val time: HabitTime,
    val isDone: Boolean,
) : DailyHabitUiModel

data class TimedHabitUiModel(
    override val id: Long,
    override val date: HabitDate,
    override val title: String,
    override val description: String?,
    override val time: HabitTime,
    val currentTime : String,
    val goalTime : String,
    val timerStatus : DailyHabitTimerStatus
) : DailyHabitUiModel

data class CountableHabitUiModel(
    override val id: Long,
    override val date: HabitDate,
    override val title: String,
    override val description: String?,
    override val time: HabitTime,
    val current : Int,
    val goal : Int
) : DailyHabitUiModel
