package af.amir.mytasky.presentation.home

sealed interface HomeScreenUiEffect {
    data object NotificationPermissionCheck: HomeScreenUiEffect
}