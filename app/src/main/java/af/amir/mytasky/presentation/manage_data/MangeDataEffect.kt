package af.amir.mytasky.presentation.manage_data

sealed interface MangeDataEffect {
    data class NavigateHabitList(val id:Long) : MangeDataEffect
    data object FinishScreen: MangeDataEffect
}