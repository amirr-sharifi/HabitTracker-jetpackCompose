package af.amir.mytasky.presentation.edit_habit

import af.amir.mytasky.presentation.model.UiMessage

sealed interface EditHabitUiEffect {
    data class ShowToastMassage(val massage : UiMessage) :EditHabitUiEffect
    data object FinishScreen : EditHabitUiEffect
    data object NotificationPermissionChecker: EditHabitUiEffect
}