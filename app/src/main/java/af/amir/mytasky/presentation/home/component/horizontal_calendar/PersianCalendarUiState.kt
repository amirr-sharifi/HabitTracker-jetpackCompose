package af.amir.mytasky.presentation.home.component.horizontal_calendar

import saman.zamani.persiandate.PersianDate

data class PersianCalendarUiState(
    val monthName: String = "",
    val currentDate : PersianDate = PersianDate.today(),
    val selectedDayIsToday : Boolean = false,
    val currentMonthDayList : List<UiDay> = emptyList(),
    val selectedDayIndex : Int = 0,
)

data class UiDay(
    val date : String,
    val dayOfWeekName : String,
    val dayOfMonth : Int,
    val isToday : Boolean
)
