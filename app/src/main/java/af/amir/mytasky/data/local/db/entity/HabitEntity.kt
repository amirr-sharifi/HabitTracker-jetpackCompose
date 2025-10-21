package af.amir.mytasky.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long? = null,
    val title : String,
    val description : String?,
    val reminderEnabled : Boolean ,
    val frequency : String,
    val repetition : String,
    val type : String,
    val createdAt : String
)
