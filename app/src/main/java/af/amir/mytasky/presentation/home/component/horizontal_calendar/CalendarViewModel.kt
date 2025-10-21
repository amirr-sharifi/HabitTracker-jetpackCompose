package af.amir.mytasky.presentation.home.component.horizontal_calendar

import af.amir.mytasky.util.getDayNameForDevice
import af.amir.mytasky.util.getMonthNameForDevice
import af.amir.mytasky.util.toPersianDate
import af.amir.mytasky.util.toStringFormat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel
@Inject
constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PersianCalendarUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffects = Channel<CalendarUiEffects>(Channel.BUFFERED)
    val uiEffects = _uiEffects.receiveAsFlow()


    init {
        setupCalender(PersianDate.today())
        selectTodayInList()
    }

    fun onEvents(event: HorizontalCalendarEvents) {
        when (event) {
            is HorizontalCalendarEvents.OnBackToday -> onBackToday()
            is HorizontalCalendarEvents.OnDayClick -> onDayClick(event.index)
            is HorizontalCalendarEvents.OnMonthChange -> onMonthChange(event.amount)
            is HorizontalCalendarEvents.OnPickDayFromDialog -> onPickDayFromDialog(event.date)
            is HorizontalCalendarEvents.OnSubDay -> onDayChange(-1)
            is HorizontalCalendarEvents.OnAddDay -> onDayChange(1)
        }
    }

    private fun onDayChange(amount: Int) {
        val currentDate = _uiState.value.currentDate
        currentDate.apply {
            if (amount > 0) {
                val monthLength = currentDate.monthLength
                if (shDay == monthLength) {
                    shMonth += 1
                    shDay = 1
                } else {
                    shDay += amount
                }
            } else {
                if (shDay == 1) {
                    shMonth -= 1
                    shDay = monthLength
                } else {
                    shDay += amount
                }
            }
        }

        setupCalender(currentDate)
        val selectedDayIndex = _uiState.value.currentMonthDayList.indexOf(currentDate.toUiDay())
        val isOnToday =
            _uiState.value.currentMonthDayList[selectedDayIndex] == PersianDate.today().toUiDay()
        _uiState.update {
            it.copy(selectedDayIndex = selectedDayIndex, selectedDayIsToday = isOnToday)
        }
        sendEffect(CalendarUiEffects.SelectedDayChange(currentDate))
    }

    private fun selectTodayInList() {
        val today = PersianDate.today()
        val index = _uiState.value.currentMonthDayList.indexOf(today.toUiDay())
        if (index < 0) return
        _uiState.update {
            it.copy(selectedDayIndex = index, selectedDayIsToday = true)
        }
        sendEffect(CalendarUiEffects.SelectedDayChange(today))
    }

    private fun onMonthChange(amount: Int) {
        val currentDate = _uiState.value.currentDate.apply { shDay = 1 }
        currentDate.apply {
            if (amount > 0) {
                if (shMonth == 12) {
                    addYear()
                    shMonth = 1
                } else {
                    shMonth += amount
                }
            }
            if (amount < 0) {
                if (shMonth == 1) {
                    subYear()
                    shMonth = 12
                } else {
                    shMonth += amount
                }
            }
        }
        setupCalender(currentDate)
        _uiState.update {
            it.copy(selectedDayIndex = -1, selectedDayIsToday = false)
        }
    }

    private fun onDayClick(index: Int) {
        if (index < 0) return
        val isOnTodayClicked =
            _uiState.value.currentMonthDayList[index] == PersianDate.today().toUiDay()
        val currentDate = _uiState.value.currentMonthDayList[index].date.toPersianDate()
        _uiState.update {
            it.copy(
                selectedDayIndex = index,
                selectedDayIsToday = isOnTodayClicked,
                currentDate = currentDate
            )
        }
        sendEffect(CalendarUiEffects.SelectedDayChange(currentDate))
    }


    private fun onPickDayFromDialog(date: PersianDate) {
        setupCalender(date)
        val selectedIndex = _uiState.value.currentMonthDayList.indexOf(date.toUiDay())
        _uiState.update {
            it.copy(
                selectedDayIndex = selectedIndex,
                selectedDayIsToday = date.isToday,
                currentDate = date
            )

        }
        sendEffect(CalendarUiEffects.SelectedDayChange(date))
    }

    private fun onBackToday() {
        val currentDate = PersianDate.today()
        setupCalender(currentDate)
        val todayIndex = _uiState.value.currentMonthDayList.indexOf(currentDate.toUiDay())
        Log.d("TodayIndex", "onBackToday: $todayIndex")
        _uiState.update {
            it.copy(
                currentDate = currentDate, selectedDayIsToday = true, selectedDayIndex = todayIndex
            )
        }
        sendEffect(CalendarUiEffects.SelectedDayChange(currentDate))
    }

    /*  private fun onBackToday() {
          setupCalender(PersianDate.today())
          val todayIndex = _uiState.value.currentMonthDayList.indexOf(PersianDate.today().toUiDay())
          Log.d("TodayIndex", "onBackToday: $todayIndex")
          _uiState.update {
              it.copy(
                  currentDate = PersianDate.today(), selectedDayIsToday = true, selectedDayIndex = todayIndex
              )
          }
      }*/

    private fun setupCalender(baseDate: PersianDate) {

        val date = PersianDate().apply {
            shYear = baseDate.shYear
            shMonth = baseDate.shMonth
            shDay = 1
        }
        val maxD = date.monthLength
        val uiDays = ArrayList<UiDay>()

        while (uiDays.size < maxD) {
            uiDays.add(date.toUiDay())
            date.addDays(1)
        }
        _uiState.update {
            it.copy(
                monthName = baseDate.getMonthNameForDevice(),
                currentMonthDayList = uiDays,
                currentDate = baseDate
            )
        }
    }


    private fun PersianDate.toUiDay(): UiDay {
        val today = PersianDate.today()
        val isToday =
            today.shDay == this.shDay && today.shMonth == this.shMonth && today.shYear == this.shYear
        return UiDay(
            date = this.toStringFormat(),
            dayOfWeekName = this.getDayNameForDevice(),
            dayOfMonth = shDay,
            isToday = isToday
        )
    }

    private fun sendEffect(effect: CalendarUiEffects) {
        viewModelScope.launch {
            _uiEffects.send(effect)
        }
    }


}


