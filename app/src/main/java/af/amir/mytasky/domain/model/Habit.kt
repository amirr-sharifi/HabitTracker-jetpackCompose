package af.amir.mytasky.domain.model

data class Habit(
    val title : String,
    val description : String?,
    val reminderEnabled : Boolean ,
    val frequency : HabitFrequency,
    val repetition : HabitRepetition,
    val type : HabitType,
    val createdAt : HabitDate,
    val id : Long? = null,
)
