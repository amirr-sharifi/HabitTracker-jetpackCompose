package af.amir.mytasky.domain.repository

import af.amir.mytasky.domain.model.DailyHabit
import af.amir.mytasky.domain.model.DailyHabitTimerStatus
import kotlinx.coroutines.flow.Flow

interface DailyHabitRepository {

    suspend fun syncHabitsForDate(date: String)
    fun getDaysHabits(date: String): Flow<List<DailyHabit>>
    suspend fun updateHabitTimer(
        habitId: Long,
        status: DailyHabitTimerStatus,
        startTime: Long,
        accumulatedTime: Long,
    )
    suspend fun getAllRunningHabits() : List<DailyHabit>
    suspend fun getDailyHabit(habitId: Long): DailyHabit?
    suspend fun doneSimpleHabit(id: Long, isDone: Boolean)
    suspend fun increaseHabitCount(id: Long,)
    suspend fun forceStopTimer(habitId: Long)


}