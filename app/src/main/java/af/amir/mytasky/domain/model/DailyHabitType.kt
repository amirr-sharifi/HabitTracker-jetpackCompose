package af.amir.mytasky.domain.model

sealed interface DailyHabitType {
    data class Simple(val isDone: Boolean) : DailyHabitType
    data class Countable(val current: Int, val goal: Int) : DailyHabitType
    data class Timed(
        val timerStatus: DailyHabitTimerStatus,
        val timerAccumulatedTimeMillis: Long,
        val goal: Long,
    ) : DailyHabitType
}