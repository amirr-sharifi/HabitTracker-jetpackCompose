package af.amir.mytasky.domain.model

sealed interface HabitFrequency {
    data class Daily (val exceptionDays : List<HabitDate>) : HabitFrequency
    data class Weekly (val daysInWeeks : List<Int>) : HabitFrequency
    data class Monthly (val daysInMonth : List<Int>) : HabitFrequency
    data class Interval(val everyXDay : Int,val echoDay : HabitDate) : HabitFrequency
}