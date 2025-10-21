package af.amir.mytasky.presentation.home

import af.amir.mytasky.R
import af.amir.mytasky.domain.model.HabitTime
import af.amir.mytasky.presentation.model.DailyHabitUiModel
import af.amir.mytasky.presentation.util.PermissionStatus
import androidx.annotation.StringRes
import org.threeten.bp.LocalTime

data class HomeScreenUiState(
    val selectedTabIndex: Int = HomeScreenTabItems.Habits.ordinal,
    val groupedHabits : Map<LocalTime,List<DailyHabitUiModel>> = emptyMap(),
    val scrollTargetIndex : Int = -1,
    val isLoading: Boolean = true,
)

enum class HomeScreenTabItems(
    @StringRes
    val strId: Int,
) {
    Habits(R.string.habits), Tasks(R.string.tasks)
}
