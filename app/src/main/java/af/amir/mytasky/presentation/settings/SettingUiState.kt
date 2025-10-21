package af.amir.mytasky.presentation.settings

import androidx.compose.ui.graphics.Color
import org.threeten.bp.LocalTime

data class SettingUiState(
    val colors : List<Color> = emptyList(),
    val selectedColorIndex : Int = 0,
    val dailyCheckReminderTime : LocalTime = LocalTime.now(),
    val dailyCheckReminderEnable : Boolean = false,
    val selectedDarkThemeIndex : Int = 0,
    val dynamicColor :Boolean= false
)
