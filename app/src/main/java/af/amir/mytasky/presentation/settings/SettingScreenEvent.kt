package af.amir.mytasky.presentation.settings

import org.threeten.bp.LocalTime

sealed interface SettingScreenEvent{
    data class OnColorClick(val index : Int): SettingScreenEvent
    data class OnDarkThemeChange(val index : Int): SettingScreenEvent
    data class OnDynamicColorChange(val value : Boolean): SettingScreenEvent
    data class OnDailyCheckReminderEnableChange(val value : Boolean): SettingScreenEvent
    data class OnDailyCheckReminderTimeChange(val time : LocalTime): SettingScreenEvent
    data class OnNotificationPermissionGranted(val isGranted :Boolean) : SettingScreenEvent
}