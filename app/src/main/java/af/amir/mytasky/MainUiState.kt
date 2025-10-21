package af.amir.mytasky

import af.amir.mytasky.util.DarkTheme
import af.amir.mytasky.util.ThemeColors

data class MainUiState(
    val isLoading : Boolean= true,
    val themeColor : ThemeColors = ThemeColors.AnotherBlue,
    val darkTheme: DarkTheme = DarkTheme.System,
    val dynamicColor : Boolean = false,
)
