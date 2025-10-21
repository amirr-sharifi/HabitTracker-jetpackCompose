package af.amir.mytasky.presentation.home

import af.amir.mytasky.domain.model.DailyHabit
import af.amir.mytasky.domain.model.DailyHabitTimerStatus
import af.amir.mytasky.domain.model.DailyHabitType
import af.amir.mytasky.domain.repository.DailyHabitRepository
import af.amir.mytasky.domain.repository.HabitRepository
import af.amir.mytasky.presentation.model.CountableHabitUiModel
import af.amir.mytasky.presentation.model.DailyHabitUiModel
import af.amir.mytasky.presentation.model.SimpleHabitUiModel
import af.amir.mytasky.presentation.model.TimedHabitUiModel
import af.amir.mytasky.presentation.timer.TimerManager
import af.amir.mytasky.presentation.util.PermissionManager
import af.amir.mytasky.presentation.util.formatMillisToUserReadable
import af.amir.mytasky.util.toLocalTime
import af.amir.mytasky.util.toStringFormat
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dailyHabitRepository: DailyHabitRepository,
    private val habitRepository: HabitRepository,
    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    private var _selectedDate = MutableStateFlow<String>("")

    private var tempTimedHabit: TimedHabitUiModel? = null
    private var notificationPermissionGranted = false

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<HomeScreenUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()


    init {

        reconcileRunningHabits()
        loadHabits()
    }

    private fun reconcileRunningHabits() {
        viewModelScope.launch {
            val runningHabits = dailyHabitRepository.getAllRunningHabits()
            runningHabits.forEach {
                if (it.id != TimerManager.currentlyActiveHabitId) {
                    dailyHabitRepository.forceStopTimer(it.id)
                }
            }
        }
    }

    private fun loadHabits() {
        val baseHabitFlow = combine(_selectedDate, habitRepository.getHabits()) { date, _ ->
            date
        }.onEach { date ->
            if (date.isNotEmpty()) dailyHabitRepository.syncHabitsForDate(date)
        }.flatMapLatest { date ->
            dailyHabitRepository.getDaysHabits(date)
        }.map { habits ->
            habits.map { it.mapToUiModel() }
        }

        val timerFlow = TimerManager.liveData
        combine(baseHabitFlow, timerFlow) { habits, timerFlow ->
            habits.map { habit ->
                if (habit is TimedHabitUiModel && habit.id == timerFlow.dailyHabitId) {
                    habit.copy(currentTime = timerFlow.displayTime, timerStatus = timerFlow.status)
                } else habit
            }
        }.onEach { habitList ->
            if (habitList.isEmpty()) {
                _uiState.update {
                    it.copy(groupedHabits = emptyMap(), isLoading = false)
                }
                return@onEach
            }
            val grouped = habitList.groupBy { it.time.time.toLocalTime().hour }.toSortedMap()
            val currentTime = LocalTime.now().hour
            val targetIndex = grouped.keys.indexOfFirst { it >= currentTime }.let {
                if (it == -1) grouped.size - 1 else it
            }

            val transformedMap = grouped.mapKeys { LocalTime.of(it.key, 0) }
            _uiState.update {
                it.copy(
                    groupedHabits = transformedMap,
                    scrollTargetIndex = targetIndex,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is HomeScreenEvent.OnDateSelected -> onDateSelected(event.date)
            is HomeScreenEvent.OnTabRowItemChange -> onTabRowItemChange(event.index)
            is HomeScreenEvent.OnHabitItemClick -> onHabitItemClick(event.habit)
            is HomeScreenEvent.OnNotificationPermissionGranted -> handleTimedHabitHabitAfterPermissionGranted(event.isGranted)
        }
    }

    private fun onHabitItemClick(habit: DailyHabitUiModel) {
        when (habit) {
            is CountableHabitUiModel -> onCountableItemClick(habit)
            is SimpleHabitUiModel -> onSimpleItemClick(habit)
            is TimedHabitUiModel -> onTimedItemClick(habit)
        }
    }

    private fun onTimedItemClick(habit: TimedHabitUiModel) {
        tempTimedHabit = habit
        if (notificationPermissionGranted)
            handleTimedHabitHabitAfterPermissionGranted(true)
        else
            sendEffect(HomeScreenUiEffect.NotificationPermissionCheck)
    }


    private fun onSimpleItemClick(habit: SimpleHabitUiModel) {
        viewModelScope.launch {
            dailyHabitRepository.doneSimpleHabit(id = habit.id, isDone = !habit.isDone)
        }
    }

    private fun handleTimedHabitHabitAfterPermissionGranted(isGranted : Boolean) {
        if (!isGranted) return
        notificationPermissionGranted = true
        tempTimedHabit?.let { habit ->
            when (habit.timerStatus) {
                DailyHabitTimerStatus.Idle, DailyHabitTimerStatus.Paused -> {
                    TimerManager.startOrResume(appContext, habit.id)
                }

                DailyHabitTimerStatus.Running -> {
                    TimerManager.pause(appContext)
                }

                DailyHabitTimerStatus.Complete -> {
                    return
                }
            }
        }
    }

    private fun onCountableItemClick(habit: CountableHabitUiModel) {
        viewModelScope.launch {
            dailyHabitRepository.increaseHabitCount(id = habit.id)
        }
    }

    private fun onTabRowItemChange(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    private fun onDateSelected(date: PersianDate) {
        if (date.toStringFormat() == _selectedDate.value) return
        _uiState.update { it.copy(isLoading = true) }
        _selectedDate.value = date.toStringFormat()


    }

    private fun sendEffect(effect: HomeScreenUiEffect) {
        viewModelScope.launch {
            _uiEffect.send(effect)
        }
    }

    private fun DailyHabit.mapToUiModel(): DailyHabitUiModel {
        return when (val type = type) {
            is DailyHabitType.Countable -> CountableHabitUiModel(
                id,
                date,
                title,
                description,
                time,
                current = type.current,
                goal = type.goal
            )

            is DailyHabitType.Simple -> SimpleHabitUiModel(
                id,
                date,
                title,
                description,
                time,
                type.isDone
            )

            is DailyHabitType.Timed -> {
                TimedHabitUiModel(
                    id,
                    date,
                    title,
                    description,
                    time,
                    currentTime = formatMillisToUserReadable(type.timerAccumulatedTimeMillis),
                    goalTime = formatMillisToUserReadable(type.goal),
                    timerStatus = type.timerStatus
                )
            }
        }
    }

}