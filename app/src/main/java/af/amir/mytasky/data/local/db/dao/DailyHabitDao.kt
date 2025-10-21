package af.amir.mytasky.data.local.db.dao

import af.amir.mytasky.data.local.db.entity.DailyHabitEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyHabitDao {

    @Transaction
    suspend fun syncHabitsForDate(
        date: String,
        newHabits: List<DailyHabitEntity>,
    ) {
        deleteByHabitIdsAndDate(date)
        insert(newHabits)
    }

    @Insert(entity = DailyHabitEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyHabit: DailyHabitEntity): Long

    @Insert(entity = DailyHabitEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyHabit: List<DailyHabitEntity>): List<Long>


    @Update(entity = DailyHabitEntity::class)
    suspend fun update(dailyHabit: DailyHabitEntity)



    @Delete(entity = DailyHabitEntity::class)
    suspend fun delete(dailyHabit: DailyHabitEntity): Int

    @Query("DELETE FROM DailyHabitEntity WHERE date =:date")
    suspend fun deleteByHabitIdsAndDate(date: String)

    @Query("SELECT * FROM DailyHabitEntity WHERE id =:id")
    suspend fun getOne(id: Long): DailyHabitEntity?

    @Query("SELECT * FROM DailyHabitEntity WHERE date =:date")
    suspend fun getByDate(date: String): List<DailyHabitEntity>

    @Query("SELECT * FROM DailyHabitEntity WHERE date =:date")
    fun getByDateFlow(date: String): Flow<List<DailyHabitEntity>>

    @Query("SELECT * FROM DailyHabitEntity")
    suspend fun getAllOneShot(): List<DailyHabitEntity>

    @Query("SELECT * FROM DailyHabitEntity WHERE timerStatus = 'Running'")
    suspend fun getAllRunningHabits() : List<DailyHabitEntity>


}