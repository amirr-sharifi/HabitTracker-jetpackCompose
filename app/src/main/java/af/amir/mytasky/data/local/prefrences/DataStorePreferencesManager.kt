package af.amir.mytasky.data.local.prefrences

import af.amir.mytasky.util.DarkTheme
import af.amir.mytasky.util.ThemeColors
import af.amir.mytasky.util.toLocalTime
import af.amir.mytasky.util.toStringFormat
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.collectLatest
import org.threeten.bp.LocalTime

data class ThemeColorPreferenceModel(
    val dynamicColor: Boolean,
    val darkTheme: DarkTheme,
    val themeColor: ThemeColors,
)

class TaskyDataStorePreferencesManager(val context: Context) {

    private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("DynamicColor")
    private val DARK_THEME_KEY = stringPreferencesKey("DarkTheme")
    private val THEME_COLOR_KEY = stringPreferencesKey("ThemeColor")
    private val REMINDER_TIME_KEY = stringPreferencesKey("ReminderTime")
    private val REMINDER_ENABLE_KEY = booleanPreferencesKey("ReminderEnable")


    suspend fun getThemeColor(onSuccess: (ThemeColorPreferenceModel) -> Unit) {
        context.dataStore.data.collectLatest {
            val dynamicColor = it[DYNAMIC_COLOR_KEY] ?: false
            val darkThemeString = it[DARK_THEME_KEY] ?: DarkTheme.System.name
            val darkTheme = DarkTheme.valueOf(darkThemeString)
            val themeColorString = it[THEME_COLOR_KEY] ?: ThemeColors.AnotherBlue.name
            val themeColor = ThemeColors.valueOf(themeColorString)

            onSuccess.invoke(
                ThemeColorPreferenceModel(
                    dynamicColor = dynamicColor,
                    darkTheme = darkTheme,
                    themeColor = themeColor
                )
            )
        }
    }

    suspend fun getDailyCheckReminderInfo(onSuccess: (time :LocalTime,enabled : Boolean) -> Unit) {
        context.dataStore.data.collectLatest {
            val time = it[REMINDER_TIME_KEY]?.toLocalTime()
            val enabled = it[REMINDER_ENABLE_KEY]
            onSuccess.invoke(time ?: LocalTime.now(),enabled?:false)
        }
    }

    suspend fun updateDailyCheckReminderTime(time: LocalTime){
        context.dataStore.edit {
            it[REMINDER_TIME_KEY] = time.toStringFormat()
        }
    }

    suspend fun updateDailyCheckReminderEnable(enabled: Boolean){
        context.dataStore.edit {
            it[REMINDER_ENABLE_KEY] = enabled
        }
    }

    suspend fun updateDarkTheme(darkColor: DarkTheme) {
        context.dataStore.edit {
            it[DARK_THEME_KEY] = darkColor.name
        }
    }

    suspend fun updateDynamicColor(dynamicColor: Boolean) {
        context.dataStore.edit {
            it[DYNAMIC_COLOR_KEY] = dynamicColor
        }
    }

    suspend fun updateThemeColor(themeColor: ThemeColors) {
        context.dataStore.edit {
            it[THEME_COLOR_KEY] = themeColor.name
        }
    }


}