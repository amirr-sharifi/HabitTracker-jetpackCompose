package af.amir.mytasky.presentation.edit_habit.components

import af.amir.mytasky.R
import af.amir.mytasky.presentation.commonComponent.persian_date_time_picker.PersianWheelDatePickerDialog
import af.amir.mytasky.presentation.commonComponent.persian_date_time_picker.PersianWheelTimePickerDialog
import af.amir.mytasky.presentation.commonComponent.persian_date_time_picker.core.SelectorProperties
import af.amir.mytasky.presentation.commonComponent.persian_date_time_picker.core.WheelPickerDefaults
import af.amir.mytasky.presentation.commonComponent.persian_date_time_picker.core.WheelTextPicker
import af.amir.mytasky.presentation.edit_habit.UIHabitFrequency
import af.amir.mytasky.presentation.edit_habit.UIHabitRepetition
import af.amir.mytasky.presentation.edit_habit.UIHabitType
import af.amir.mytasky.presentation.theme.StandardRoundedShape
import af.amir.mytasky.util.toStringFormat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalTime
import saman.zamani.persiandate.PersianDate
import java.util.Locale


@Composable
private fun ContentContainer(
    modifier: Modifier = Modifier,
    titleText: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    shape: Shape = StandardRoundedShape,
    contentColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    content: @Composable () -> Unit,
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .background(contentColor, shape)
            .clip(shape)
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        TitleBoldText(title = titleText, textColor = textColor)
        Spacer(Modifier.height(4.dp))
        content()
    }

}

@Composable
private fun TitleTextField(
    modifier: Modifier = Modifier,
    text: String,
    maxLine: Int,
    singleLine: Boolean = false,
    onTextChange: (title: String) -> Unit,
) {

    TextField(
        modifier = modifier.fillMaxWidth(),
        value = text,
        onValueChange = { onTextChange(it) },
        maxLines = maxLine,
        singleLine = singleLine,
        minLines = 1,
        colors = TextFieldDefaults.colors(
            disabledIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            errorContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = StandardRoundedShape
    )
}

@Composable
private fun TitleBoldText(title: String, textColor: Color = MaterialTheme.colorScheme.onSurface) {
    Text(
        text = title,
        color = textColor,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun SelectableButton(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    buttonsShape: Shape = RoundedCornerShape(12.dp),
    buttonText: String,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primary,
    selectedTextColor: Color = MaterialTheme.colorScheme.onPrimary,
    unselectedContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    unselectedTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onItemClick: () -> Unit,
) {
    val backgroundColor =
        if (isSelected) selectedContainerColor else unselectedContainerColor
    Box(
        modifier = modifier
            .padding(vertical = 4.dp)
            .background(backgroundColor, shape = buttonsShape)
            .clip(shape = buttonsShape)
            .clickable {
                onItemClick()
            }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        val textColor = if (isSelected) selectedTextColor else unselectedTextColor
        Text(text = buttonText, color = textColor)
    }
}


@Composable
fun HabitTitleChapter(
    modifier: Modifier = Modifier,
    titleText: String,
    descriptionText: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer, StandardRoundedShape)
            .clip(StandardRoundedShape)
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        TitleBoldText(stringResource(R.string.title))
        Spacer(Modifier.height(4.dp))
        TitleTextField(
            text = titleText,
            maxLine = 1,
            singleLine = true,
            onTextChange = onTitleChange
        )
        Spacer(modifier = Modifier.height(8.dp))

        TitleBoldText(stringResource(R.string.description))
        Spacer(Modifier.height(4.dp))
        TitleTextField(
            text = descriptionText,
            maxLine = 3,
            singleLine = false,
            onTextChange = onDescriptionChange
        )


    }
}

@Composable
fun HabitTypeChapter(
    modifier: Modifier = Modifier,
    currentHabitType: UIHabitType,
    currentCountHabitNumber: Int,
    currentHour: Int,
    currentMinute: Int,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onHabitTypeClick: (UIHabitType) -> Unit,
    onPlusButtonClick: () -> Unit,
    onMinusButtonClick: () -> Unit,
    onSnappedHour: (Int) -> Unit,
    onSnappedMinute: (Int) -> Unit,
) {


    ContentContainer(
        modifier.animateContentSize(),
        titleText = stringResource(R.string.habit_type)
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            UIHabitType.entries.forEach {
                val selected = it == currentHabitType
                SelectableButton(
                    modifier = Modifier.weight(1f),
                    isSelected = selected,
                    buttonsShape = StandardRoundedShape,
                    buttonText = stringResource(it.stringResId),
                ) {
                    onHabitTypeClick(it)
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        AnimatedContent(
            targetState = currentHabitType,
            transitionSpec = {
                slideInVertically { it } + fadeIn() togetherWith slideOutVertically { it } + fadeOut()
            }, label = "habitType"
        ) { type ->
            when (type) {
                UIHabitType.Simple -> Spacer(Modifier.height(0.dp))
                UIHabitType.Countable -> CounterView(
                    currentCountHabitNumber,
                    textColor,
                    onMinusButtonClick,
                    onPlusButtonClick
                )

                UIHabitType.Timed -> TimeWheelView(
                    currentHour,
                    currentMinute,
                    textColor,
                    (0..12).toList(),
                    (1..59).toList(),
                    onSnappedHour,
                    onSnappedMinute
                )

            }
        }


    }

}

@Composable
private fun CounterView(
    currentCountHabitNumber: Int,
    textColor: Color,
    onMinusButtonClick: () -> Unit,
    onPlusButtonClick: () -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onMinusButtonClick,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(text = "-", color = textColor)
                }
                Spacer(Modifier.width(8.dp))
                Text(text = "$currentCountHabitNumber", fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = onPlusButtonClick,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(text = "+", color = textColor)
                }
            }

        }

    }
}

@Composable
private fun TimeWheelView(
    currentHour: Int,
    currentMinute: Int,
    textColor: Color,
    hours: List<Int>,
    minutes: List<Int>,
    onSnappedHour: (Int) -> Unit,
    onSnappedMinute: (Int) -> Unit,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    size: DpSize = DpSize(128.dp, 128.dp),
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(stringResource(R.string.hour_with_symbol))

            Box(contentAlignment = Alignment.Center) {

                if (selectorProperties.enabled().value) {
                    Surface(
                        modifier = Modifier.size(size.width, size.height / 3),
                        shape = selectorProperties.shape().value,
                        color = selectorProperties.color().value,
                        border = selectorProperties.border().value
                    ) {}
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WheelTextPicker(
                        size = DpSize(
                            width = size.width / 2, height = size.height
                        ),
                        texts = hours.map { String.format("%02d", it) },
                        rowCount = 3,
                        color = textColor,
                        selectorProperties = WheelPickerDefaults.selectorProperties(
                            enabled = false
                        ),
                        startIndex = hours.indexOfFirst { it == currentHour },
                        onScrollFinished = { snappedIndex ->
                            if (currentHour != hours.getOrNull(snappedIndex))
                                onSnappedHour(hours.getOrNull(snappedIndex) ?: 0)
                            snappedIndex
                        }
                    )
                    Text(text = ":", fontWeight = FontWeight.Bold)
                    WheelTextPicker(
                        size = DpSize(
                            width = size.width / 2, height = size.height
                        ),
                        texts = minutes.map { String.format("%02d", it) },
                        rowCount = 3,
                        color = textColor,
                        selectorProperties = WheelPickerDefaults.selectorProperties(
                            enabled = false
                        ),
                        startIndex = minutes.indexOf(currentMinute),
                        onScrollFinished = { snappedIndex ->
                            if (currentMinute != minutes.getOrNull(snappedIndex))
                                onSnappedMinute(minutes.getOrNull(snappedIndex) ?: 0)
                            snappedIndex
                        }
                    )

                }
            }


            Text(stringResource(R.string.minute_with_symbol))
        }

    }

}

@Composable
fun HabitFrequencyChapter(
    modifier: Modifier = Modifier,
    currentUIHabitFrequency: UIHabitFrequency,
    dailyExceptionDays: List<String>,
    selectedWeeklyIndices: List<Int>,
    selectedMonthlyIndices: List<Int>,
    intervalHabitCurrentValue: Int,
    intervalHabitStartDay: PersianDate,
    onAddDailyExceptionDay: (PersianDate) -> Unit,
    onRemoveDailyExceptionDay: (index: Int) -> Unit,
    onToggleWeekDay: (index: Int) -> Unit,
    onToggleMonthDay: (index: Int) -> Unit,
    onIntervalHabitSnappedValue: (Int) -> Unit,
    onIntervalHabitStartDayChange: (PersianDate) -> Unit,
    onHabitFrequencyClick: (UIHabitFrequency) -> Unit,
) {
    var showDayPickerDialog by rememberSaveable { mutableStateOf(false) }

    ContentContainer(
        modifier.animateContentSize(),
        titleText = stringResource(R.string.habit_frequency)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(), maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UIHabitFrequency.entries.forEach {
                val isSelected = it == currentUIHabitFrequency
                SelectableButton(
                    modifier = Modifier.weight(1f),
                    isSelected = isSelected,
                    buttonText = stringResource(it.stringResId),
                ) {
                    onHabitFrequencyClick(it)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        AnimatedContent(
            targetState = currentUIHabitFrequency, transitionSpec = {
                slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut()
            }, label = "habitFrequency"
        ) { frequency ->
            when (frequency) {
                UIHabitFrequency.Daily -> SelectableListRow(
                    titleText = stringResource(R.string.exception_days),
                    items = dailyExceptionDays,
                    onAddItemClick = { showDayPickerDialog = true },
                    onRemoveItemClick = onRemoveDailyExceptionDay
                )


                UIHabitFrequency.Weekly -> {
                    val daysOfWeeks = DayOfWeek.entries.map {
                        it.getDisplayName(
                            org.threeten.bp.format.TextStyle.FULL,
                            Locale.getDefault()
                        )
                    }

                    DayOfXSelector(
                        titleText = stringResource(R.string.week_days),
                        selectedIndices = selectedWeeklyIndices,
                        items = daysOfWeeks,
                        onItemClick = onToggleWeekDay
                    )
                }

                UIHabitFrequency.Monthly -> DayOfXSelector(
                    titleText = stringResource(R.string.month_days),
                    selectedIndices = selectedMonthlyIndices,
                    items = (1..31).toList().map { it.toString() },
                    onItemClick = onToggleMonthDay
                )

                UIHabitFrequency.Interval -> HabitIntervalView(
                    currentValue = intervalHabitCurrentValue,
                    startDay = intervalHabitStartDay,
                    onSnappedValue = onIntervalHabitSnappedValue,
                    onStartDayChange = onIntervalHabitStartDayChange
                )
            }
        }
    }
    PersianWheelDatePickerDialog(
        showDatePicker = showDayPickerDialog,
        onDismiss = { showDayPickerDialog = false }) {
        onAddDailyExceptionDay(it)
        showDayPickerDialog = false
    }

}

@Composable
private fun SelectableListRow(
    modifier: Modifier = Modifier,
    titleText: String,
    addButtonContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    addButtonContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    items: List<String>,
    onAddItemClick: () -> Unit,
    onRemoveItemClick: (index: Int) -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column {
            TitleBoldText(title = titleText, textColor = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { onAddItemClick() },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = addButtonContainerColor,
                        contentColor = addButtonContentColor
                    )
                ) {
                    Text("+", color = addButtonContentColor)
                }
                VerticalDivider(
                    thickness = 1.dp,
                    color = addButtonContentColor,
                    modifier = Modifier.fillMaxHeight(0.7f)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    itemsIndexed(items) { index, item ->
                        Row(
                            Modifier
                                .background(
                                    addButtonContainerColor,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clip(shape = RoundedCornerShape(12.dp))
                                .padding(start = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item)
                            IconButton(onClick = { onRemoveItemClick(index) }) {
                                Icon(Icons.Rounded.Clear, null)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayOfXSelector(
    titleText: String,
    items: List<String>,
    selectedIndices: List<Int>,
    onItemClick: (index: Int) -> Unit,
) {
    Column {
        TitleBoldText(titleText, MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(6.dp))
        LazyRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            itemsIndexed(items) { index, item ->
                SelectableButton(isSelected = selectedIndices.contains(index), buttonText = item) {
                    onItemClick(index)
                }
            }
        }
    }
}

@Composable
private fun HabitIntervalView(
    currentValue: Int,
    startDay: PersianDate,
    onSnappedValue: (Int) -> Unit,
    onStartDayChange: (PersianDate) -> Unit,
) {
    val valueList = (1..29).toList()
    var showDayPickerDialog by rememberSaveable { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.every))
        WheelTextPicker(
            size = DpSize(48.dp, 64.dp),
            texts = valueList.map { it.toString() },
            rowCount = 3,
            selectorProperties = WheelPickerDefaults.selectorProperties(
                enabled = false
            ),
            startIndex = valueList.indexOf(currentValue),
            onScrollFinished = { snappedIndex ->
                if (currentValue != valueList.getOrNull(snappedIndex))
                    onSnappedValue(valueList.getOrNull(snappedIndex) ?: 0)
                snappedIndex
            }
        )
        Spacer(Modifier.width(4.dp))
        Text(stringResource(R.string.day_from))
        Spacer(Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(shape = RoundedCornerShape(12.dp))
                .clickable {
                    showDayPickerDialog = true
                }
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = startDay.toStringFormat())
        }
    }

    PersianWheelDatePickerDialog(showDatePicker = showDayPickerDialog, onDismiss = {
        showDayPickerDialog = false
    }, onDoneClick = {
        onStartDayChange(it)
        showDayPickerDialog = false
    })

}

@Composable
fun HabitRepetitionChapter(
    modifier: Modifier = Modifier,
    currentRepetition: UIHabitRepetition,
    multipleHabitTimeList: List<String>,
    onceHabitCurrentHour: Int,
    onceHabitCurrentMinute: Int,
    reminderEnable: Boolean,
    onSnappedOnceHabitCurrentHour: (Int) -> Unit,
    onSnappedOnceHabitCurrentMinute: (Int) -> Unit,
    onRepetitionClick: (UIHabitRepetition) -> Unit,
    onMultipleHabitAddTime: (LocalTime) -> Unit,
    onMultipleHabitRemoveTime: (index: Int) -> Unit,
    onReminderChange: (Boolean) -> Unit,
) {
    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }

    ContentContainer(
        modifier = modifier.animateContentSize(),
        titleText = stringResource(R.string.repetition_in_day)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UIHabitRepetition.entries.forEach {
                SelectableButton(
                    modifier = Modifier.weight(1f),
                    isSelected = it == currentRepetition,
                    buttonText = stringResource(it.stringResId)
                ) {
                    onRepetitionClick(it)
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        AnimatedContent(
            modifier = Modifier.fillMaxWidth(),
            targetState = currentRepetition,
            transitionSpec = {
                slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut()
            }, label = "habitRepetition"
        ) { repetition ->
            when (repetition) {
                UIHabitRepetition.Once -> TimeWheelView(
                    textColor = MaterialTheme.colorScheme.onSurface,
                    hours = (0..23).toList(),
                    minutes = (0..59).toList(),
                    currentHour = onceHabitCurrentHour,
                    currentMinute = onceHabitCurrentMinute,
                    onSnappedHour = onSnappedOnceHabitCurrentHour,
                    onSnappedMinute = onSnappedOnceHabitCurrentMinute
                )

                UIHabitRepetition.Multiple -> SelectableListRow(
                    titleText = stringResource(R.string.times),
                    items = multipleHabitTimeList,
                    onAddItemClick = { showTimePickerDialog = true },
                    onRemoveItemClick = onMultipleHabitRemoveTime
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.reminder))
            Switch(checked = reminderEnable, onCheckedChange = onReminderChange)
        }
    }
    PersianWheelTimePickerDialog(
        showDatePicker = showTimePickerDialog,
        onDismiss = { showTimePickerDialog = false }) {
        onMultipleHabitAddTime(it)
        showTimePickerDialog = false
    }

}


@Composable
fun UpdateAndDeleteButton(
    modifier: Modifier = Modifier,
    contentColor: Color,
    containerColor: Color,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {

    val bColors = ButtonDefaults.buttonColors(
        contentColor = contentColor,
        containerColor = containerColor
    )

    Row(
        modifier = modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth(0.8f)
            .height(IntrinsicSize.Min)
            .clip(shape = RoundedCornerShape(12.dp)),
    ) {
        Button(
            shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
            onClick = { onUpdateClick() },
            modifier = Modifier
                .weight(8f)
                .fillMaxHeight(),
            colors = bColors
        ) {
            Text(
                text = stringResource(R.string.update),
                color = contentColor,
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .background(
                    color = containerColor,
                    shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                )
                .clip(shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                .clickable {
                    onDeleteClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "",
                modifier = Modifier.size(24.dp),
                tint = contentColor
            )
        }

    }


}
