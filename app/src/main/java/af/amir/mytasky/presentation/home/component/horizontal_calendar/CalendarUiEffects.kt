package af.amir.mytasky.presentation.home.component.horizontal_calendar

import saman.zamani.persiandate.PersianDate

sealed interface CalendarUiEffects {
    data class SelectedDayChange(val date : PersianDate) : CalendarUiEffects
}