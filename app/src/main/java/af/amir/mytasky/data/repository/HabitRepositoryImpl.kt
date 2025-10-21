package af.amir.mytasky.data.repository

import af.amir.mytasky.data.local.db.dao.HabitDao
import af.amir.mytasky.data.mapper.DAILY_FREQUENCY
import af.amir.mytasky.data.mapper.INTERVAL_FREQUENCY
import af.amir.mytasky.data.mapper.MONTHLY_FREQUENCY
import af.amir.mytasky.data.mapper.WEEKLY_FREQUENCY
import af.amir.mytasky.data.mapper.toDomain
import af.amir.mytasky.data.mapper.toEntity
import af.amir.mytasky.domain.model.Habit
import af.amir.mytasky.domain.model.HabitFrequency
import af.amir.mytasky.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val dao: HabitDao,
) : HabitRepository {
    override suspend fun insert(habit: Habit): Long = dao.insert(habit.toEntity())

    override suspend fun deleteHabit(habit: Habit): Int = dao.delete(habit.toEntity())

    override suspend fun updateHabit(habit: Habit): Int = dao.update(habit.toEntity())

    override suspend fun getHabitById(id: Long): Habit? = dao.getById(id)?.toDomain()

    override fun getHabits(): Flow<List<Habit>> {
        return dao.getAllFlow().map { list -> list.map { it.toDomain() } }
    }

    override fun getDailyHabits(): Flow<List<Habit>> {
        val habits = dao.getHabitsByFrequency(DAILY_FREQUENCY)
        return habits.map { habitEntities -> habitEntities.map { it.toDomain() } }
    }

    override fun getWeeklyHabits(): Flow<List<Habit>> {
        val habits = dao.getHabitsByFrequency(WEEKLY_FREQUENCY)
        return habits.map { habitEntities -> habitEntities.map { it.toDomain() } }
    }

    override fun getDayOfWeekHabits(day: Int): Flow<List<Habit>> {
        val habits = dao.getHabitsByFrequencyAndValue(WEEKLY_FREQUENCY, day)
        return habits.map { habitEntities -> habitEntities.map { it.toDomain() } }
    }

    override fun getWeeklyHabitExistingDays(): Flow<List<Int>> {
        return getWeeklyHabits().map { habitEntities ->
            habitEntities.flatMap { (it.frequency as HabitFrequency.Weekly).daysInWeeks }.distinct()
        }

    }


    override fun getMonthlyHabit(): Flow<List<Habit>> {
        val habits = dao.getHabitsByFrequency(MONTHLY_FREQUENCY)
        return habits.map { habitEntities -> habitEntities.map { it.toDomain() } }
    }

    override fun getDayOfMonthHabit(day: Int): Flow<List<Habit>> {
        val habits = dao.getHabitsByFrequencyAndValue(MONTHLY_FREQUENCY, day)
        return habits.map { habitEntities -> habitEntities.map { it.toDomain() } }
    }

    override fun getMonthlyHabitExistingDays(): Flow<List<Int>> {
        return getMonthlyHabit().map { habitEntities ->
            habitEntities.flatMap { (it.frequency as HabitFrequency.Monthly).daysInMonth }
                .distinct()
        }
    }


    override fun getIntervalHabits(): Flow<List<Habit>> {
        val habits = dao.getHabitsByFrequency(INTERVAL_FREQUENCY)
        return habits.map { habitEntities -> habitEntities.map { it.toDomain() } }
    }



}