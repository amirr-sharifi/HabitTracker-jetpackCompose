package af.amir.mytasky.domain.model

sealed interface HabitType {
    data object Simple : HabitType
    data class Countable(val goal : Int) : HabitType
    data class Timed(val goal : Long) : HabitType
}