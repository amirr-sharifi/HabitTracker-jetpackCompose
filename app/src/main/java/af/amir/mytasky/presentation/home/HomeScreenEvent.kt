package af.amir.mytasky.presentation.home

import af.amir.mytasky.presentation.model.DailyHabitUiModel
import android.app.Activity
import saman.zamani.persiandate.PersianDate

sealed interface HomeScreenEvent {
    data class OnDateSelected(val date: PersianDate) : HomeScreenEvent
    data class OnTabRowItemChange(val index: Int) : HomeScreenEvent
    data class OnHabitItemClick(val habit: DailyHabitUiModel) : HomeScreenEvent
    data class OnNotificationPermissionGranted (val isGranted : Boolean): HomeScreenEvent

}