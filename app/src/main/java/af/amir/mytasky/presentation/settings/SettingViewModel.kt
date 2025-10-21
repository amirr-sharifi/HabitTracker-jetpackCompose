package af.amir.mytasky.presentation.settings

import af.amir.mytasky.data.local.prefrences.TaskyDataStorePreferencesManager
import af.amir.mytasky.frameworks.managers.ReminderManager
import af.amir.mytasky.presentation.theme.colors.anotherBlueLightScheme
import af.amir.mytasky.presentation.theme.colors.blueLightScheme
import af.amir.mytasky.presentation.theme.colors.greenLightScheme
import af.amir.mytasky.presentation.theme.colors.pinkLightScheme
import af.amir.mytasky.presentation.theme.colors.purpleLightScheme
import af.amir.mytasky.util.DarkTheme
import af.amir.mytasky.util.ThemeColors
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val preferencesManager: TaskyDataStorePreferencesManager,
    private val reminderManager: ReminderManager,
) : ViewModel() {
    
    
    private var tempReminderValue: Boolean? = null
    private var notificationPermissionGranted = false

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState get() = _uiState.asStateFlow()

    private val _uiEffect = Channel<SettingScreenUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()



    init {
        viewModelScope.launch(IO) {
            preferencesManager.getDailyCheckReminderInfo { time, enable ->
                _uiState.update {
                    it.copy(dailyCheckReminderTime = time, dailyCheckReminderEnable = enable)
                }
            }

        }


        val colorMutableSet = mutableSetOf<Color>()
        ThemeColors.entries.forEach {
            val color =
                when (it) {
                    ThemeColors.AnotherBlue -> anotherBlueLightScheme.primary
                    ThemeColors.Blue -> blueLightScheme.primary
                    ThemeColors.Green -> greenLightScheme.primary
                    ThemeColors.Pink -> pinkLightScheme.primary
                    ThemeColors.Purple -> purpleLightScheme.primary
                }

            colorMutableSet.add(color)
        }

        viewModelScope.launch(IO) {
            preferencesManager.getThemeColor {themeInfo->
                _uiState.update {currentState->
                    currentState.copy(
                        dynamicColor = themeInfo.dynamicColor,
                        selectedColorIndex = themeInfo.themeColor.ordinal,
                        selectedDarkThemeIndex = themeInfo.darkTheme.ordinal,
                        colors = colorMutableSet.toList()
                    )
                }
            }
        }


    }

    fun onEvent(event: SettingScreenEvent) {
        when (event) {
            is SettingScreenEvent.OnColorClick -> {
                viewModelScope.launch {
                    val themeColor = ThemeColors.entries[event.index]
                    preferencesManager.updateThemeColor(themeColor)
                    _uiState.update { it.copy(selectedColorIndex = event.index) }
                }
            }

            is SettingScreenEvent.OnDarkThemeChange -> {
                viewModelScope.launch {
                    val darkTheme = DarkTheme.entries[event.index]
                    preferencesManager.updateDarkTheme(darkTheme)
                }
            }

            is SettingScreenEvent.OnDynamicColorChange -> {
                viewModelScope.launch {
                    preferencesManager.updateDynamicColor(event.value)
                    _uiState.update { it.copy(dynamicColor = event.value) }
                }
            }

            is SettingScreenEvent.OnDailyCheckReminderEnableChange -> notificationPermissionCheck(event.value)


            is SettingScreenEvent.OnDailyCheckReminderTimeChange -> {
                viewModelScope.launch {
                    val time = event.time
                    preferencesManager.updateDailyCheckReminderTime(time)
                    reminderManager.setDailyCheckAlarm(time)
                }
            }

            is SettingScreenEvent.OnNotificationPermissionGranted -> onNotificationPermissionGranted(event.isGranted)
        }
    }

    private fun notificationPermissionCheck(enable: Boolean) {
        tempReminderValue = enable
        if (notificationPermissionGranted){
            onDailyCheckReminderEnableChange(enable)
        }else{
            viewModelScope.launch {
                _uiEffect.send(SettingScreenUiEffect.NotificationPermissionCheck)
            }
        }
    }

    private fun onDailyCheckReminderEnableChange(enable: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateDailyCheckReminderEnable(enable)
            if (enable) {
                reminderManager.setDailyCheckAlarm(_uiState.value.dailyCheckReminderTime)
            } else {
                reminderManager.cancelDailyCheckAlarm()
            }
        }
    }

    private fun onNotificationPermissionGranted(isGranted : Boolean) {
        if(isGranted){
            notificationPermissionGranted = true
            tempReminderValue?.let{
                onDailyCheckReminderEnableChange(it)
            }
        }
    }


}

