package af.amir.mytasky.presentation.edit_habit

import af.amir.mytasky.R
import af.amir.mytasky.domain.model.Habit
import af.amir.mytasky.domain.model.HabitDate
import af.amir.mytasky.domain.model.HabitFrequency
import af.amir.mytasky.domain.model.HabitRepetition
import af.amir.mytasky.domain.model.HabitTime
import af.amir.mytasky.domain.model.HabitType
import af.amir.mytasky.domain.repository.HabitRepository
import af.amir.mytasky.frameworks.managers.ReminderManager
import af.amir.mytasky.presentation.model.UiMessage
import af.amir.mytasky.presentation.root_graph.KEY_ID
import af.amir.mytasky.util.toLocalTime
import af.amir.mytasky.util.toPersianDate
import af.amir.mytasky.util.toStringFormat
import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime
import saman.zamani.persiandate.PersianDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class EditHabitViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val reminderManger: ReminderManager,
    private val repository: HabitRepository,
    @ApplicationContext val context: Context,
) : ViewModel() {

    private val habitId: Long = savedStateHandle[KEY_ID] ?: -1L
    private var notificationPermissionChecked = false
    private var tempReminderValue : Boolean? = null

    private val _state: MutableStateFlow<EditHabitState> = MutableStateFlow(EditHabitState())
    val state: StateFlow<EditHabitState> = _state.asStateFlow()

    private val _uiEffect: Channel<EditHabitUiEffect> = Channel(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        initRawData(habitId)
        viewModelScope.launch {
            repository.getHabits().collect {
                Log.e("InsertHabit", "list : $it ")
            }
        }
    }


    fun onEvent(event: EditHabitEvent) {
        when (event) {
            EditHabitEvent.OnSaveHabit -> onCreateHabit()
            is EditHabitEvent.OnDescriptionChange -> onDescriptionTextChange(event.description)
            is EditHabitEvent.OnTitleChange -> onTitleTextChange(event.title)
            is EditHabitEvent.OnHabitTypeChange -> onHabitTypeChange(event.type)
            EditHabitEvent.OnDecreaseCount -> onDecreaseCountHabit()
            EditHabitEvent.OnIncreaseCount -> onIncreaseCountHabit()
            is EditHabitEvent.OnHourTimedHabitChange -> onHourTimedHabitChange(event.hour)
            is EditHabitEvent.OnMinuteTimedHabitChange -> onMinuteTimedHabitChange(event.minute)
            is EditHabitEvent.OnHabitFrequencyChange -> onHabitFrequencyChange(event.frequency)
            is EditHabitEvent.OnAddDailyHabitExceptionDay -> onAddDailyHabitExceptionDay(event.day)
            is EditHabitEvent.OnRemoveDailyHabitExceptionDay -> onRemoveDailyHabitExceptionDay(event.index)
            is EditHabitEvent.OnToggleDayOfMonthlyHabit -> onToggleDayOfMonthlyHabit(event.index)
            is EditHabitEvent.OnToggleDayOfWeeklyHabit -> onToggleDayOfWeeklyHabit(event.index)
            is EditHabitEvent.OnIntervalHabitStartDayChange -> onIntervalHabitStartDayChange(event.day)
            is EditHabitEvent.OnIntervalHabitValueChange -> onIntervalHabitValueChange(event.value)

            is EditHabitEvent.OnHabitRepetitionInDayChange -> onHabitRepetitionInDayChange(event.repetition)
            is EditHabitEvent.OnAddRepetitionTime -> onAddTimeToMultipleRepetitionHabit(
                event.time
            )

            is EditHabitEvent.OnRemoveRepetitionTime -> onRemoveTimeToMultipleRepetitionHabit(
                event.index
            )

            is EditHabitEvent.OnHourRepetitionInDayChange -> onHourRepetitionInDayChange(event.hour)
            is EditHabitEvent.OnMinuteRepetitionInDayChange -> onMinuteRepetitionInDayChange(event.minute)
            is EditHabitEvent.OnReminderEnableChange -> onNotificationPermissionCheck(event.enable)

            EditHabitEvent.OnDeleteHabit -> onDeleteHabit()
            is EditHabitEvent.OnNotificationPermissionGranted -> onNotificationPermissionGranted(event.isGranted)
        }
    }

    private fun onNotificationPermissionGranted(isGranted : Boolean) {
        if(isGranted){
            notificationPermissionChecked= true
            tempReminderValue?.let {
                onReminderEnableChange(it)
            }
        }
    }

    private fun onDeleteHabit() {
        if (habitId == -1L) return
        viewModelScope.launch {
            val habit = repository.getHabitById(habitId) ?: return@launch
            repository.deleteHabit(habit)
            sendUiEffect(EditHabitUiEffect.FinishScreen)
        }
    }


    private fun initRawData(habitId: Long) {
        if (habitId == -1L) return
        viewModelScope.launch {
            val habit = repository.getHabitById(habitId) ?: return@launch
            tempReminderValue = habit.reminderEnabled
            var newState = EditHabitState(
                isLoading = true,
                updateMode = true,
                title = habit.title,
                description = habit.description ?: "",
                reminderEnabled = habit.reminderEnabled
            )
            newState = when (val type = habit.type) {
                is HabitType.Countable -> {
                    newState.copy(habitType = UIHabitType.Countable, habitCount = type.goal)
                }

                HabitType.Simple -> {
                    newState.copy(habitType = UIHabitType.Simple)
                }

                is HabitType.Timed -> {
                    val habitTimedGoal = millisToTime(type.goal)
                    newState.copy(
                        habitType = UIHabitType.Timed,
                        habitTimedHour = habitTimedGoal.first,
                        habitTimedMinute = habitTimedGoal.second
                    )
                }
            }

            newState = when (val frequency = habit.frequency) {
                is HabitFrequency.Daily -> {
                    val exDays = frequency.exceptionDays.map { it.date.toPersianDate() }
                    newState.copy(
                        habitFrequency = UIHabitFrequency.Daily,
                        habitDailyExceptionDays = exDays
                    )
                }

                is HabitFrequency.Interval -> {
                    newState.copy(
                        habitFrequency = UIHabitFrequency.Interval,
                        habitIntervalStartDay = frequency.echoDay.date.toPersianDate(),
                        habitIntervalXNumber = frequency.everyXDay
                    )
                }

                is HabitFrequency.Monthly -> {
                    val mDays = frequency.daysInMonth.map { it }
                    newState.copy(
                        habitFrequency = UIHabitFrequency.Monthly,
                        habitMonthlyDays = mDays
                    )
                }

                is HabitFrequency.Weekly -> {
                    newState.copy(
                        habitFrequency = UIHabitFrequency.Weekly,
                        habitWeeklyDays = frequency.daysInWeeks
                    )
                }
            }

            newState = when (val repetition = habit.repetition) {
                is HabitRepetition.Multiple -> {
                    val times = repetition.times.map { it.time.toLocalTime() }
                    newState.copy(
                        habitRepetition = UIHabitRepetition.Multiple,
                        multipleHabitTimes = times
                    )
                }

                is HabitRepetition.Once -> {
                    val time = repetition.time.time.toLocalTime()
                    newState.copy(
                        habitRepetition = UIHabitRepetition.Once,
                        onceTimeHabitHour = time.hour,
                        onceTimeHabitMinute = time.minute
                    )

                }
            }
            _state.emit(newState.copy(isLoading = false))
        }
    }

    private fun onCreateHabit() {
        val currentState = _state.value
        if (currentState.title == "" || currentState.title.isBlank()) {
            sendUiEffect(EditHabitUiEffect.ShowToastMassage(UiMessage.Resource(R.string.please_enter_a_title)))
            return
        }

        val title = currentState.title
        val description = currentState.description
        val type = when (currentState.habitType) {
            UIHabitType.Simple -> HabitType.Simple
            UIHabitType.Countable -> HabitType.Countable(currentState.habitCount)
            UIHabitType.Timed -> {
                HabitType.Timed(
                    timeToMillis(
                        currentState.habitTimedHour,
                        currentState.habitTimedMinute
                    )
                )
            }
        }
        val frequency = when (currentState.habitFrequency) {
            UIHabitFrequency.Daily -> {
                val exceptionDays =
                    currentState.habitDailyExceptionDays.map { HabitDate(it.toStringFormat()) }
                HabitFrequency.Daily(exceptionDays)
            }

            UIHabitFrequency.Weekly -> {
                val days = currentState.habitWeeklyDays
                HabitFrequency.Weekly(days)
            }

            UIHabitFrequency.Monthly -> {
                val days = currentState.habitMonthlyDays
                HabitFrequency.Monthly(days)
            }

            UIHabitFrequency.Interval -> {
                HabitFrequency.Interval(
                    currentState.habitIntervalXNumber,
                    HabitDate(currentState.habitIntervalStartDay.toStringFormat())
                )
            }
        }
        val repetition = when (currentState.habitRepetition) {
            UIHabitRepetition.Once -> {
                val time =
                    LocalTime.of(currentState.onceTimeHabitHour, currentState.onceTimeHabitMinute)
                HabitRepetition.Once(HabitTime(time.toStringFormat()))
            }

            UIHabitRepetition.Multiple -> {
                val times = currentState.multipleHabitTimes.map { HabitTime(it.toStringFormat()) }
                HabitRepetition.Multiple(times)
            }
        }
        val reminder = currentState.reminderEnabled

        val newHabit = Habit(
            title,
            description,
            reminder,
            frequency,
            repetition,
            type,
            HabitDate(PersianDate.today().toStringFormat())
        )

        sendUiEffect(EditHabitUiEffect.FinishScreen)
        viewModelScope.launch {
            if (habitId == -1L) {
                val result = repository.insert(newHabit)
                if (result != -1L && reminder) {
                    repository.getHabitById(result)?.let {
                        reminderManger.setHabitReminder(it)
                    }
                }
            } else {
                val updatedHabit = newHabit.copy(id = habitId)
                repository.updateHabit(updatedHabit)
            }
        }
    }

    private fun onRemoveTimeToMultipleRepetitionHabit(index: Int) {
        val toRemoveItem = _state.value.multipleHabitTimes.getOrNull(index) ?: return
        _state.update {
            it.copy(multipleHabitTimes = _state.value.multipleHabitTimes - toRemoveItem)
        }
    }

    private fun onNotificationPermissionCheck(enable: Boolean){
        tempReminderValue = enable
        if (notificationPermissionChecked){
            onReminderEnableChange(enable)
        }else{
            sendUiEffect(EditHabitUiEffect.NotificationPermissionChecker)
        }
    }

    private fun onReminderEnableChange(enable: Boolean) {

        if (notificationPermissionChecked) {
            _state.update {
                it.copy(reminderEnabled = enable)
            }
        } else {

        }

    }

    private fun onMinuteRepetitionInDayChange(minute: Int) {
        _state.update {
            it.copy(onceTimeHabitMinute = minute)
        }
    }

    private fun onHourRepetitionInDayChange(hour: Int) {
        _state.update {
            it.copy(onceTimeHabitHour = hour)
        }
    }

    private fun onHabitRepetitionInDayChange(repetition: UIHabitRepetition) {
        _state.update {
            it.copy(habitRepetition = repetition)
        }
    }

    private fun onAddTimeToMultipleRepetitionHabit(time: LocalTime) {
        val sameItem = _state.value.multipleHabitTimes.find {
            it.hour == time.hour && it.minute == time.minute
        }
        if (sameItem != null) return
        _state.update {
            it.copy(multipleHabitTimes = _state.value.multipleHabitTimes + time)
        }

    }

    private fun onIntervalHabitValueChange(value: Int) {
        _state.update {
            it.copy(habitIntervalXNumber = value)
        }
    }

    private fun onIntervalHabitStartDayChange(day: PersianDate) {
        _state.update {
            it.copy(habitIntervalStartDay = day)
        }
    }

    private fun onToggleDayOfWeeklyHabit(index: Int) {

        val exist = _state.value.habitWeeklyDays.contains(index)
        val newList = _state.value.habitWeeklyDays.toMutableList()
        if (exist) {
            newList.remove(index)
        } else {
            newList.add(index)
        }
        _state.update { it.copy(habitWeeklyDays = newList) }
    }

    private fun onToggleDayOfMonthlyHabit(index: Int) {
        val exist = _state.value.habitMonthlyDays.contains(index)
        val newList = _state.value.habitMonthlyDays.toMutableList()
        if (exist) {
            newList.remove(index)
        } else {
            newList.add(index)
        }
        _state.update { it.copy(habitMonthlyDays = newList) }
    }

    private fun onRemoveDailyHabitExceptionDay(index: Int) {
        val item = _state.value.habitDailyExceptionDays.getOrNull(index) ?: return
        _state.update {
            it.copy(habitDailyExceptionDays = _state.value.habitDailyExceptionDays - item)
        }
    }

    private fun onAddDailyHabitExceptionDay(day: PersianDate) {
        val sameItem = _state.value.habitDailyExceptionDays.find {
            it.shDay == day.shDay && it.shMonth == day.shMonth && it.shYear == day.shYear
        }
        if (sameItem != null) return
        _state.update {
            it.copy(habitDailyExceptionDays = _state.value.habitDailyExceptionDays + day)
        }
    }

    private fun onHabitFrequencyChange(frequency: UIHabitFrequency) {
        _state.update {
            it.copy(habitFrequency = frequency)
        }
    }

    private fun onMinuteTimedHabitChange(minute: Int) {
        _state.update {
            it.copy(habitTimedMinute = minute)
        }
    }

    private fun onHourTimedHabitChange(hour: Int) {
        _state.update {
            it.copy(habitTimedHour = hour)
        }
    }

    private fun onIncreaseCountHabit() {
        val currentCount = _state.value.habitCount
        _state.update {
            it.copy(habitCount = currentCount + 1)
        }
    }

    private fun onDecreaseCountHabit() {
        val currentCount = _state.value.habitCount
        if (currentCount <= 2) return
        _state.update {
            it.copy(habitCount = currentCount - 1)
        }
    }

    private fun onHabitTypeChange(type: UIHabitType) {
        _state.update {
            it.copy(habitType = type)
        }
    }

    private fun onDescriptionTextChange(description: String) {
        _state.update {
            it.copy(description = description)
        }
    }

    private fun onTitleTextChange(title: String) {
        _state.update {
            it.copy(title = title)
        }
    }

    private fun sendUiEffect(effect: EditHabitUiEffect) {
        viewModelScope.launch {
            _uiEffect.send(effect)
        }
    }

    private fun timeToMillis(hour: Int, minute: Int): Long {
        val hourInMillis = TimeUnit.HOURS.toMillis(hour.toLong())
        val minuteInMillis = TimeUnit.MINUTES.toMillis(minute.toLong())
        return hourInMillis + minuteInMillis

    }

    private fun millisToTime(millis: Long): Pair<Int, Int> {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        return (hours.toInt() to minutes.toInt())
    }

}
