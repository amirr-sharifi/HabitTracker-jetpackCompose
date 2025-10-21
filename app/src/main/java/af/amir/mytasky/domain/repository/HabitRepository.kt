package af.amir.mytasky.domain.repository

import af.amir.mytasky.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {

    suspend fun insert(habit : Habit) : Long
    suspend fun deleteHabit(habit: Habit) : Int
    suspend fun updateHabit(habit: Habit) : Int
    suspend fun getHabitById(id: Long) : Habit?
    fun getHabits() : Flow<List<Habit>>
     fun getDailyHabits() : Flow<List<Habit>>
     fun getWeeklyHabits() : Flow<List<Habit>>
     fun getDayOfWeekHabits(day : Int) : Flow<List<Habit>>
    fun getWeeklyHabitExistingDays() : Flow<List<Int>>
     fun getMonthlyHabit() : Flow<List<Habit>>
    fun getDayOfMonthHabit(day : Int ) : Flow<List<Habit>>
    fun getMonthlyHabitExistingDays() : Flow<List<Int>>
     fun getIntervalHabits() : Flow<List<Habit>>

}