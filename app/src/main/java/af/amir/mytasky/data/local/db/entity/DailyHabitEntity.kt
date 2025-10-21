package af.amir.mytasky.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyHabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val date : String,
    val habitId : Long,
    val title : String,
    val description : String?,
    val time : String,
    val type : String,
    val timerGoal :Long,
    val timerStatus : String?,
    val timerStartTimeMillis : Long = 0L,
    val timerAccumulatedTimeMillis : Long?,
    val isDone : Boolean? ,
    val counterGoal : Int,
    val currentCount : Int?
) {
    companion object

}
