package af.amir.mytasky.presentation.settings

sealed interface SettingScreenUiEffect {
    data object NotificationPermissionCheck: SettingScreenUiEffect
}