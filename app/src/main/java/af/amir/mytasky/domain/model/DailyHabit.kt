package af.amir.mytasky.domain.model


data class DailyHabit(
    val id : Long,
    val date: HabitDate,
    val title : String,
    val description : String?,
    val time : HabitTime,
    val type : DailyHabitType,
    val timerStartTimeMillis :Long,
    val habitId : Long
)
