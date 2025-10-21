package af.amir.mytasky.presentation.settings

import af.amir.mytasky.R
import af.amir.mytasky.presentation.commonComponent.TimeSelectorButton
import af.amir.mytasky.presentation.commonComponent.permission_handler.NotificationPermissionChecker
import af.amir.mytasky.util.DarkTheme
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.threeten.bp.LocalTime

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {

    val viewModel: SettingViewModel = hiltViewModel()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = state.colors
    val selectedColorIndex = state.selectedColorIndex
    val dynamicColor = state.dynamicColor
    val selectedDarkThemeIndex = state.selectedDarkThemeIndex
    val dailyCheckReminderEnabled = state.dailyCheckReminderEnable
    val dailyCheckReminderTime = state.dailyCheckReminderTime


    var permissionChecker by remember { mutableStateOf(false) }
    if (permissionChecker){
        NotificationPermissionChecker {
            viewModel.onEvent(SettingScreenEvent.OnNotificationPermissionGranted(it))
            permissionChecker = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect{
            when(it){
                SettingScreenUiEffect.NotificationPermissionCheck -> permissionChecker = true
            }
        }
    }

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom  = 16.dp)) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            text = stringResource(R.string.settings),
            textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold
        )

        ThemeChapter(
            colors = colors,
            colorsSelectedIndex = selectedColorIndex,
            darkThemeSelectedIndex = selectedDarkThemeIndex,
            dynamicColor = dynamicColor,
            onEvent = viewModel::onEvent
        )

        Spacer(Modifier.height(32.dp))

        ReminderChapter(
            selectedTime = dailyCheckReminderTime,
            reminderEnabled = dailyCheckReminderEnabled,
            onTimeChanged = {
                viewModel.onEvent(SettingScreenEvent.OnDailyCheckReminderTimeChange(it))
            },
            onReminderEnableChanged = {
                viewModel.onEvent(SettingScreenEvent.OnDailyCheckReminderEnableChange(it))
            })

    }

}

@Composable
fun ThemeChapter(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    colorsSelectedIndex: Int,
    dynamicColor: Boolean,
    darkThemeSelectedIndex: Int,
    onEvent: (event: SettingScreenEvent) -> Unit,
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 56.dp)
    ) {
        TitleText(text = stringResource(R.string.theme))
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {

            Column {
                Text(text = stringResource(R.string.choose_color))
                Spacer(modifier = Modifier.height(8.dp))
                /*Color Picker *******************/
                ColorPicker(colors, colorsSelectedIndex) {
                    onEvent(SettingScreenEvent.OnColorClick(it))
                }
                HorizontalDivider()

                //**Dark theme***********************************//
                DarkThemeChooser(
                    text = stringResource(R.string.dark_theme),
                    selectedIndex = darkThemeSelectedIndex
                ) {
                    onEvent(SettingScreenEvent.OnDarkThemeChange(it))
                }

                HorizontalDivider()

                //**Dynamic color***********************************//
                val enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                SwitchWithText(
                    enabled = enabled,
                    checked = dynamicColor,
                    text = stringResource(R.string.dynamic_color)
                ) {
                    onEvent(SettingScreenEvent.OnDynamicColorChange(it))
                }

            }


        }
    }

}

@Composable
private fun ColorPicker(
    colors: List<Color>,
    colorsSelectedIndex: Int,
    onColorClick: (index: Int) -> Unit,
) {
    LazyRow(contentPadding = PaddingValues(horizontal = 4.dp)) {
        itemsIndexed(colors) { index, item ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
                    .size(32.dp)
                    .background(item, shape = CircleShape)
                    .clip(
                        shape = CircleShape
                    )
                    .clickable {
                        onColorClick(index)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (colorsSelectedIndex == index)
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
            }
        }
    }
}

@Composable
private fun DarkThemeChooser(
    modifier: Modifier = Modifier,
    text: String,
    selectedIndex: Int,
    onItemClick: (index: Int) -> Unit,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text)
        Spacer(Modifier.width(8.dp))
        LazyRow {
            items(DarkTheme.entries) { darkItem ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .clickable { onItemClick(darkItem.ordinal) }
                ) {
                    Text(text = stringResource(darkItem.stringResId))
                    Spacer(Modifier.width(4.dp))
                    Box(
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedIndex == darkItem.ordinal)
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.Default.Check,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                    }
                }
            }
        }
    }

}

@Composable
private fun TitleText(
    modifier: Modifier = Modifier,
    text: String,
    fontWeight: FontWeight = FontWeight.Bold,
    fontSize: TextUnit = MaterialTheme.typography.titleMedium.fontSize,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    Text(
        text = text,
        modifier = modifier.padding(start = 16.dp),
        color = textColor,
        fontWeight = fontWeight,
        fontSize = fontSize
    )
}


@Composable
fun SwitchWithText(
    modifier: Modifier = Modifier,
    checked: Boolean,
    text: String,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier
            .background(shape = RoundedCornerShape(12.dp), color = Color.Transparent)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text)
        Spacer(modifier = Modifier.width(16.dp))
        Switch(checked = checked, onCheckedChange = {
            onCheckedChange(it)
        }, enabled = enabled)
        Spacer(modifier = Modifier.width(4.dp))
        if (!enabled) {
            Text(
                text = stringResource(R.string.your_device_doesnt_support),
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ReminderChapter(
    modifier: Modifier = Modifier,
    selectedTime: LocalTime,
    reminderEnabled: Boolean,
    onReminderEnableChanged: (Boolean) -> Unit,
    onTimeChanged: (LocalTime) -> Unit,
) {
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground
    val textColor by
    animateColorAsState(
        if (reminderEnabled) onBackgroundColor else onBackgroundColor.copy(alpha = 0.5f),
        tween(500), label = "color"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        TitleText(text = stringResource(R.string.daily_check_notification))
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Column (modifier = Modifier.height(IntrinsicSize.Min)) {
                SwitchWithText(
                    modifier = Modifier.weight(1f),
                    checked = reminderEnabled,
                    text = stringResource(R.string.notification),
                    onCheckedChange = onReminderEnableChanged
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text(text = stringResource(R.string.time), color = textColor)
                    Spacer(Modifier.width(16.dp).padding(end = 8.dp))
                    TimeSelectorButton(
                        horizontalPadding = 24.dp,
                        selectedTime = selectedTime,
                        textColor = textColor,
                        shape = CircleShape,
                        borderColor = MaterialTheme.colorScheme.secondary,
                        onTimePickerResult = onTimeChanged,
                        enabled = reminderEnabled
                    )
                }
            }

        }
    }
}