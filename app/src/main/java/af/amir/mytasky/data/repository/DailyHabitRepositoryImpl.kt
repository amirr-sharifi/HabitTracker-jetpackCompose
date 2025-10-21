package af.amir.mytasky.data.repository

import af.amir.mytasky.data.local.db.dao.DailyHabitDao
import af.amir.mytasky.data.local.db.dao.HabitDao
import af.amir.mytasky.data.local.db.entity.DailyHabitEntity
import af.amir.mytasky.data.local.db.entity.HabitEntity
import af.amir.mytasky.data.mapper.toDbString
import af.amir.mytasky.data.mapper.toDomain
import af.amir.mytasky.data.mapper.toEntity
import af.amir.mytasky.data.util.isRelevantToDay
import af.amir.mytasky.domain.model.DailyHabit
import af.amir.mytasky.domain.model.DailyHabitTimerStatus
import af.amir.mytasky.domain.model.DailyHabitType
import af.amir.mytasky.domain.model.Habit
import af.amir.mytasky.domain.model.HabitRepetition
import af.amir.mytasky.domain.model.HabitType
import af.amir.mytasky.domain.repository.DailyHabitRepository
import android.util.Log
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DailyHabitRepositoryImpl @Inject constructor(
    private val dailyHabitDao: DailyHabitDao,
    private val habitDao: HabitDao,
) : DailyHabitRepository {



    override suspend fun syncHabitsForDate(date: String) {

        val sourceHabits = habitDao.getAllOneShot()
        val currentDailyHabitsForDate = dailyHabitDao.getByDate(date)

        val progressMap = currentDailyHabitsForDate.associateBy { it.habitId to it.time }

        val finalDailyHabits = mutableListOf<DailyHabitEntity>()
        sourceHabits.forEach {sourceHabitEntity->
            val idealHabits = convertHabitsToDailyHabits(sourceHabitEntity,date)
            val mergedHabits = idealHabits.map{idealHabit->
                val savedProgress = progressMap[idealHabit.habitId to idealHabit.time]
                if (savedProgress != null){
                    idealHabit.copy(
                        timerStatus = savedProgress.timerStatus,
                        timerStartTimeMillis = savedProgress.timerStartTimeMillis,
                        timerAccumulatedTimeMillis = savedProgress.timerAccumulatedTimeMillis,
                        timerGoal = savedProgress.timerGoal,
                        currentCount = savedProgress.currentCount,
                        counterGoal = savedProgress.counterGoal,
                        isDone = savedProgress.isDone
                    )
                }else idealHabit
            }
            finalDailyHabits.addAll(mergedHabits)
        }

        dailyHabitDao.syncHabitsForDate(date,finalDailyHabits)
    }

    private fun convertHabitsToDailyHabits(
        habitEntity: HabitEntity,
        date: String,
    ): List<DailyHabitEntity> {
        val habit = habitEntity.toDomain()
        return when (val rep = habit.repetition) {
            is HabitRepetition.Once -> listOf(
                DailyHabitEntity.createFrom(
                    habit,
                    date,
                    rep.time.time
                )
            )

            is HabitRepetition.Multiple -> rep.times.map { t ->
                DailyHabitEntity.createFrom(
                    habit,
                    date,
                    t.time
                )
            }
        }
    }


    override fun getDaysHabits(date: String): Flow<List<DailyHabit>> {
        return dailyHabitDao.getByDateFlow(date).map { entities ->
            entities.map {
                it.toDomain()
            }
        }

    }

    override suspend fun forceStopTimer(habitId: Long) {
        withContext(IO) {
            val habitEntity = dailyHabitDao.getOne(habitId) ?: return@withContext

            if (habitEntity.timerStatus == DailyHabitTimerStatus.Running.name) {

                val stoppedEntity = habitEntity.copy(
                    timerStatus = DailyHabitTimerStatus.Paused.name,
                    timerStartTimeMillis = 0L,
                )

                dailyHabitDao.update(stoppedEntity)
            }
        }
    }

    override suspend fun updateHabitTimer(
        habitId: Long,
        status: DailyHabitTimerStatus,
        startTime: Long,
        accumulatedTime: Long,
    ) {
        val habit = getDailyHabit(habitId) ?: return
        if (habit.type !is DailyHabitType.Timed) return
        val newHabit = habit.copy(
            type = habit.type.copy(
                timerStatus = status,
                timerAccumulatedTimeMillis = accumulatedTime
            ), timerStartTimeMillis = startTime
        )

        dailyHabitDao.update(newHabit.toEntity())
    }

    override suspend fun getAllRunningHabits(): List<DailyHabit> {
        return dailyHabitDao.getAllRunningHabits().map { it.toDomain() }
    }

    override suspend fun getDailyHabit(habitId: Long): DailyHabit? {
        return dailyHabitDao.getOne(habitId)?.toDomain()
    }


    override suspend fun doneSimpleHabit(id: Long, isDone: Boolean) {
        val daily = dailyHabitDao.getOne(id)?.toDomain() ?: return
        if (daily.type !is DailyHabitType.Simple) return
        val newType = daily.type.copy(isDone = isDone)
        val newHabit = daily.copy(type = newType)
        dailyHabitDao.update(newHabit.toEntity())


    }

    override suspend fun increaseHabitCount(id: Long) {
        val daily = dailyHabitDao.getOne(id)?.toDomain() ?: return
        if (daily.type !is DailyHabitType.Countable) return
        if (daily.type.goal == daily.type.current) return
        val newType = daily.type.copy(current = daily.type.current + 1)
        val newHabit = daily.copy(type = newType)
        dailyHabitDao.update(newHabit.toEntity())

    }


}


private fun HabitType.toDailyHabitType(): DailyHabitType {
    return when (this) {
        HabitType.Simple -> DailyHabitType.Simple(false)
        is HabitType.Countable -> DailyHabitType.Countable(0, goal)
        is HabitType.Timed -> DailyHabitType.Timed(DailyHabitTimerStatus.Idle, 0L, goal)
    }
}

private fun DailyHabitEntity.Companion.createFrom(
    habit: Habit,
    date: String,
    time: String,
): DailyHabitEntity {
    val baseDailyHabit = DailyHabitEntity(
        date = date,
        habitId = habit.id ?: 0,
        title = habit.title,
        description = habit.description,
        time = time,
        type = habit.type.toDailyHabitType().toDbString(),
        timerStatus = null,
        timerStartTimeMillis = 0L,
        timerAccumulatedTimeMillis = 0L,
        timerGoal = 0L,
        isDone = false,
        currentCount = 0,
        counterGoal = 0
    )
    return when (val type = habit.type) {
        is HabitType.Countable -> baseDailyHabit.copy(counterGoal = type.goal)
        HabitType.Simple -> baseDailyHabit
        is HabitType.Timed -> baseDailyHabit.copy(timerGoal = type.goal)
    }
}
