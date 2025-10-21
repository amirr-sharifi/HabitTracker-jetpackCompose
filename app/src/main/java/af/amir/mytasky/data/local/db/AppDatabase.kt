package af.amir.mytasky.data.local.db

import af.amir.mytasky.data.local.db.dao.DailyHabitDao
import af.amir.mytasky.data.local.db.dao.HabitDao
import af.amir.mytasky.data.local.db.entity.DailyHabitEntity
import af.amir.mytasky.data.local.db.entity.HabitEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ HabitEntity::class,DailyHabitEntity::class], exportSchema = false, version = 1)
abstract class AppDatabase :RoomDatabase(){

    abstract val habitDao : HabitDao
    abstract val dailyHabitDao : DailyHabitDao

}