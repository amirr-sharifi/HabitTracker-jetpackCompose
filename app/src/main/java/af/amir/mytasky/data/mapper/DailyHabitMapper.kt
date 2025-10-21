package af.amir.mytasky.data.mapper

import af.amir.mytasky.data.local.db.entity.DailyHabitEntity
import af.amir.mytasky.domain.model.DailyHabit
import af.amir.mytasky.domain.model.DailyHabitTimerStatus
import af.amir.mytasky.domain.model.DailyHabitType
import af.amir.mytasky.domain.model.HabitDate
import af.amir.mytasky.domain.model.HabitTime
import af.amir.mytasky.domain.model.HabitType
import android.util.Log

fun DailyHabit.toEntity(): DailyHabitEntity {
    val dailyHabit = DailyHabitEntity(
        id = id,
        date = date.date,
        habitId = habitId,
        title = title,
        description = description,
        time = time.time,
        type = type.toDbString(),
        timerStartTimeMillis = timerStartTimeMillis,
        timerStatus = null,
        timerAccumulatedTimeMillis = 0L,
        timerGoal = 0L,
        isDone = false,
        currentCount = 0,
        counterGoal = 0
    )

    return when (type) {
        is DailyHabitType.Countable -> dailyHabit.copy(
            currentCount = type.current,
            counterGoal = type.goal
        )

        is DailyHabitType.Simple -> dailyHabit.copy(isDone = type.isDone)
        is DailyHabitType.Timed -> dailyHabit.copy(
            timerStatus = type.timerStatus.name,
            timerAccumulatedTimeMillis = type.timerAccumulatedTimeMillis,
            timerGoal = type.goal
        )
    }

}

fun DailyHabitEntity.toDomain(): DailyHabit {
    val dailyHabitType = when ( this.type.toDailyHabitType()) {
        is DailyHabitType.Countable -> DailyHabitType.Countable(
            this.currentCount ?: 0,
            this.counterGoal
        )

        is DailyHabitType.Simple -> DailyHabitType.Simple(this.isDone ?: false)
        is DailyHabitType.Timed -> DailyHabitType.Timed(
            goal = this.timerGoal,
            timerAccumulatedTimeMillis = this.timerAccumulatedTimeMillis ?: 0L,
            timerStatus = try {
                DailyHabitTimerStatus.valueOf(this.timerStatus ?: DailyHabitTimerStatus.Idle.name)
            } catch (e: IllegalArgumentException) {
                DailyHabitTimerStatus.Idle
            }
        )
    }

    return DailyHabit(
        id = this.id,
        date = HabitDate(this.date),
        title = this.title,
        description = this.description,
        time = HabitTime(this.time),
        type = dailyHabitType,
        habitId = this.habitId,
        timerStartTimeMillis = this.timerStartTimeMillis
    )
}
