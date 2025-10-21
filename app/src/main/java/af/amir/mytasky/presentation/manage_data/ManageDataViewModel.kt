package af.amir.mytasky.presentation.manage_data

import af.amir.mytasky.domain.repository.HabitRepository
import af.amir.mytasky.presentation.manage_data.model.CategoryDisplayItem
import af.amir.mytasky.presentation.manage_data.model.CategoryFrequency
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class ManageDataViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val repository: HabitRepository,
) : ViewModel() {


    private val _uiEffect = Channel<MangeDataEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    private val stack: MutableStateFlow<List<ManageDataUiState>> =
        MutableStateFlow(listOf(ManageDataUiState()))

    val displayItems = stack.map { it.last().currentList }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        fetchBaseData()
    }

    private fun fetchBaseData() {
        val items = CategoryFrequency.entries.map {
            CategoryDisplayItem.Frequency(
                context.getString(it.resId),
                it,
            )
        }
        val initialState = ManageDataUiState(
            currentList = items,
            currentPage = ManageDataCategories.FREQUENCY
        )
        stack.value = listOf(initialState)
    }

    fun onEvents(event: ManageDataEvent) {
        when (event) {
            is ManageDataEvent.OnItemClick -> onItemClick(event.item)
            ManageDataEvent.OnBack -> handleBackClick()
        }
    }

    private fun handleBackClick() {
        if (stack.value.size == 1) {
            sendUiEffect(MangeDataEffect.FinishScreen)
        } else {
            val list = stack.value
            val topToShow = list[list.size - 2]
            if (topToShow.currentList.isEmpty())
                fetchBaseData()
            else
                stack.update { it.dropLast(1) }
        }
    }

    private fun onItemClick(item: CategoryDisplayItem) {
        when (item) {
            is CategoryDisplayItem.Frequency -> loadStateForFrequency(item.frequency)
            is CategoryDisplayItem.DayOfWeek -> loadStateForDayOfWeek(item.value)
            is CategoryDisplayItem.DayOfMonth -> loadStateForDayOfMonth(item.shDay)
            is CategoryDisplayItem.Habit -> sendUiEffect(MangeDataEffect.NavigateHabitList(item.id))
        }
    }


    private fun loadStateForDayOfMonth(shDay: Int) {
        viewModelScope.launch {
            repository.getDayOfMonthHabit(shDay - 1).collect { habits ->
                val categoryDisplayList =
                    habits.map { CategoryDisplayItem.Habit(it.title, it.id ?: -1) }
                pushStack(
                    ManageDataUiState(
                        currentList = categoryDisplayList,
                        currentPage = ManageDataCategories.DAYOFMONTH
                    )
                )
            }
        }
    }

    private fun loadStateForDayOfWeek(value: Int) {
        viewModelScope.launch {
            repository.getDayOfWeekHabits(value).collect { habits ->
                val categoryDisplayList =
                    habits.map { CategoryDisplayItem.Habit(it.title, it.id ?: -1) }
                pushStack(
                    ManageDataUiState(
                        currentList = categoryDisplayList,
                        currentPage = ManageDataCategories.DAYOFWEEK
                    )
                )
            }
        }
    }

    private fun loadStateForFrequency(type: CategoryFrequency) {
        when (type) {
            CategoryFrequency.DAILY -> {
                viewModelScope.launch {
                    repository.getDailyHabits().collect { habits ->
                        val categoryDisplayList =
                            habits.map { CategoryDisplayItem.Habit(it.title, it.id ?: -1) }
                        pushStack(
                            ManageDataUiState(
                                currentList = categoryDisplayList,
                                currentPage = ManageDataCategories.HABIT
                            )
                        )
                    }
                }
            }

            CategoryFrequency.WEEKLY -> {
                viewModelScope.launch {
                    repository.getWeeklyHabitExistingDays().collect { dayOfWeeks ->
                        val categoryDisplayList =
                            dayOfWeeks.map {
                                CategoryDisplayItem.DayOfWeek(
                                    DayOfWeek.entries[it].name,
                                    it
                                )
                            }
                        pushStack(
                            ManageDataUiState(
                                currentList = categoryDisplayList,
                                currentPage = ManageDataCategories.HABIT
                            )
                        )
                    }
                }
            }

            CategoryFrequency.MONTHLY -> {
                viewModelScope.launch {
                    repository.getMonthlyHabitExistingDays().collect { dayOfMonth ->
                        val categoryDisplayList =
                            dayOfMonth.map {
                                CategoryDisplayItem.DayOfMonth(
                                    (it + 1).toString(),
                                    it + 1
                                )
                            }
                        pushStack(
                            ManageDataUiState(
                                currentList = categoryDisplayList,
                                currentPage = ManageDataCategories.HABIT
                            )
                        )
                    }
                }
            }

            CategoryFrequency.INTERVAL -> {
                viewModelScope.launch {
                    repository.getIntervalHabits().collect { habits ->
                        val categoryDisplayList =
                            habits.map { CategoryDisplayItem.Habit(it.title, it.id ?: -1) }
                        pushStack(
                            ManageDataUiState(
                                currentList = categoryDisplayList,
                                currentPage = ManageDataCategories.HABIT
                            )
                        )
                    }
                }
            }
        }
    }


    private fun sendUiEffect(effect: MangeDataEffect) {
        viewModelScope.launch {
            _uiEffect.send(effect)
        }
    }

    private fun pushStack(newState: ManageDataUiState) {
        val top = stack.value.last()

        stack.update {
            if (top.currentPage == newState.currentPage)
                it.dropLast(1) + newState
            else
                it + newState
        }
    }


}


enum class ManageDataCategories {
    FREQUENCY, DAYOFWEEK, DAYOFMONTH, INTERVAL, HABIT
}

data class ManageDataUiState(
    val currentList: List<CategoryDisplayItem> = emptyList(),
    val currentPage: ManageDataCategories = ManageDataCategories.FREQUENCY,
)





