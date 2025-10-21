package af.amir.mytasky.domain.model

sealed interface HabitRepetition {
    data class Once(val time : HabitTime) : HabitRepetition
    data class Multiple(val times: List<HabitTime>): HabitRepetition
}