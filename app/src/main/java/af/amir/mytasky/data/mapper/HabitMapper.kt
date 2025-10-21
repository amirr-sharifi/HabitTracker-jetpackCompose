package af.amir.mytasky.data.mapper

import af.amir.mytasky.data.local.db.entity.HabitEntity
import af.amir.mytasky.domain.model.Habit
import af.amir.mytasky.domain.model.HabitDate
import af.amir.mytasky.domain.model.HabitTime

fun Habit.toEntity(): HabitEntity =
    HabitEntity(
        id = id,
        title = title,
        description = description,
        reminderEnabled = reminderEnabled,
        frequency = frequency.toDbString(),
        repetition = repetition.toDbString(),
        type = type.toDbString(),
        createdAt = createdAt.date
    )

fun HabitEntity.toDomain() : Habit =
    Habit(
        id = id,
        title = title,
        description = description,
        reminderEnabled = reminderEnabled,
        frequency = frequency.toHabitFrequency(),
        repetition = repetition.toHabitRepetition(),
        type = type.toHabitType(),
        createdAt = HabitDate(createdAt)
    )
