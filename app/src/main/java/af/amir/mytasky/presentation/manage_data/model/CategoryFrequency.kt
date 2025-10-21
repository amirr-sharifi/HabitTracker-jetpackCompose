package af.amir.mytasky.presentation.manage_data.model

import af.amir.mytasky.R
import androidx.annotation.StringRes

enum class CategoryFrequency(
    @StringRes
    val resId: Int,
) {
    DAILY(R.string.daily), WEEKLY(R.string.weekly), MONTHLY(R.string.monthly), INTERVAL(R.string.everyxday)
}

sealed interface CategoryDisplayItem {
    val label: String

    data class Frequency(
        override val label: String, val frequency: CategoryFrequency,
    ) : CategoryDisplayItem

    data class DayOfWeek(
        override val label: String, val value: Int,
    ) : CategoryDisplayItem

    data class DayOfMonth(
        override val label: String, val shDay: Int,
    ) : CategoryDisplayItem


    data class Habit(
        override val label: String,
        val id: Long,
    ) : CategoryDisplayItem
}

