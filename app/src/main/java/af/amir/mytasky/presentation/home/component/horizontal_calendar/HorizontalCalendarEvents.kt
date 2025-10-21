package af.amir.mytasky.presentation.home.component.horizontal_calendar

import saman.zamani.persiandate.PersianDate

sealed interface HorizontalCalendarEvents {
    class OnMonthChange(val amount: Int) : HorizontalCalendarEvents
    class OnDayClick(val index: Int) : HorizontalCalendarEvents
    class OnBackToday() : HorizontalCalendarEvents
    data class OnPickDayFromDialog(val date : PersianDate) : HorizontalCalendarEvents
    data object OnAddDay : HorizontalCalendarEvents
    data object OnSubDay : HorizontalCalendarEvents
}