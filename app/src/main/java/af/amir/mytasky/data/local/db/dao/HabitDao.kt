package af.amir.mytasky.data.local.db.dao

import af.amir.mytasky.data.local.db.entity.HabitEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert
    suspend fun insert(habit : HabitEntity) : Long

    @Delete
    suspend fun delete(habit : HabitEntity) : Int

    @Update
    suspend fun update(habit: HabitEntity) : Int

    @Query("SELECT * FROM HabitEntity")
    fun getAllFlow(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM HabitEntity")
    suspend fun getAllOneShot(): List<HabitEntity>

    @Query("SELECT * FROM HabitEntity WHERE id =:id")
    suspend fun getById(id : Long): HabitEntity?

    @Query("SELECT * FROM HabitEntity WHERE frequency LIKE '%'||:frequency||'%'")
     fun getHabitsByFrequency(frequency :String) : Flow<List<HabitEntity>>

    @Query("SELECT * FROM HabitEntity WHERE frequency LIKE '%'||:frequency||'%' AND frequency LIKE '%'||:value||'%'")
    fun getHabitsByFrequencyAndValue(frequency :String,value: Int) : Flow<List<HabitEntity>>


}