package af.amir.mytasky.presentation.theme

import af.amir.mytasky.presentation.theme.colors.anotherBlueDarkScheme
import af.amir.mytasky.presentation.theme.colors.anotherBlueLightScheme
import af.amir.mytasky.presentation.theme.colors.blueDarkScheme
import af.amir.mytasky.presentation.theme.colors.blueLightScheme
import af.amir.mytasky.presentation.theme.colors.greenDarkScheme
import af.amir.mytasky.presentation.theme.colors.greenLightScheme
import af.amir.mytasky.presentation.theme.colors.pinkDarkScheme
import af.amir.mytasky.presentation.theme.colors.pinkLightScheme
import af.amir.mytasky.presentation.theme.colors.purpleDarkScheme
import af.amir.mytasky.presentation.theme.colors.purpleLightScheme
import af.amir.mytasky.util.ThemeColors
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext





@Composable
fun MyTaskyTheme(
    themeColor: ThemeColors,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> {
            when(themeColor){
                ThemeColors.AnotherBlue -> anotherBlueDarkScheme
                ThemeColors.Blue -> blueDarkScheme
                ThemeColors.Green -> greenDarkScheme
                ThemeColors.Pink -> pinkDarkScheme
                ThemeColors.Purple -> purpleDarkScheme
            }
        }
        else -> {
            when(themeColor){
                ThemeColors.AnotherBlue -> anotherBlueLightScheme
                ThemeColors.Blue -> blueLightScheme
                ThemeColors.Green -> greenLightScheme
                ThemeColors.Pink -> pinkLightScheme
                ThemeColors.Purple -> purpleLightScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

