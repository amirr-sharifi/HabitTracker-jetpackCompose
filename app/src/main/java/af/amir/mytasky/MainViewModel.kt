package af.amir.mytasky

import af.amir.mytasky.data.local.prefrences.TaskyDataStorePreferencesManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: TaskyDataStorePreferencesManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState get() = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            preferencesManager.getThemeColor {
                val themeColor = it.themeColor
                val dynamicColor = it.dynamicColor
                val darkTheme = it.darkTheme
                _uiState.update {
                    it.copy(
                        themeColor = themeColor,
                        darkTheme = darkTheme,
                        dynamicColor = dynamicColor,
                        isLoading = false
                    )
                }
            }
        }
    }


}