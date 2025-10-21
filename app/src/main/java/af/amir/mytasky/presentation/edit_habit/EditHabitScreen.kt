package af.amir.mytasky.presentation.edit_habit

import af.amir.mytasky.R
import af.amir.mytasky.presentation.commonComponent.permission_handler.NotificationPermissionChecker
import af.amir.mytasky.presentation.edit_habit.components.HabitFrequencyChapter
import af.amir.mytasky.presentation.edit_habit.components.HabitRepetitionChapter
import af.amir.mytasky.presentation.edit_habit.components.HabitTitleChapter
import af.amir.mytasky.presentation.edit_habit.components.HabitTypeChapter
import af.amir.mytasky.presentation.edit_habit.components.UpdateAndDeleteButton
import af.amir.mytasky.presentation.theme.StandardRoundedShape
import af.amir.mytasky.util.toStringFormat
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun NewEditTaskHabitScreen(
    modifier: Modifier = Modifier,
    viewModel: EditHabitViewModel,
    onFinishScreen: () -> Unit,
) {

    var permissionChecker by remember { mutableStateOf(false) }
    if (permissionChecker) {
        NotificationPermissionChecker {
            viewModel.onEvent(EditHabitEvent.OnNotificationPermissionGranted(it))
            permissionChecker = false
        }
    }

    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is EditHabitUiEffect.ShowToastMassage -> {
                    Toast.makeText(context, effect.massage.asString(context), Toast.LENGTH_SHORT)
                        .show()
                }

                EditHabitUiEffect.FinishScreen -> onFinishScreen()
                EditHabitUiEffect.NotificationPermissionChecker -> permissionChecker = true

            }
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(scrollBehavior = scrollBehavior, title = {
                Text(
                    stringResource(
                        R.string.habit
                    ), fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            }, navigationIcon = {
                IconButton(onClick = onFinishScreen) {
                    Icon(Icons.Rounded.ArrowBack, null)
                }
            })
        }) { paddingValue ->

        if (state.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValue), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else
            EditScreenContent(Modifier.padding(paddingValue), state, viewModel::onEvent)

    }

}

@Composable
private fun EditScreenContent(
    modifier: Modifier = Modifier,
    state: EditHabitState,
    onEvent: (EditHabitEvent) -> Unit,
) {
    LazyColumn(
        modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {

        item {
            HabitTitleChapter(
                titleText = state.title,
                descriptionText = state.description,
                onTitleChange = { onEvent(EditHabitEvent.OnTitleChange(it)) },
                onDescriptionChange = { onEvent(EditHabitEvent.OnDescriptionChange(it)) },
            )
            Spacer(Modifier.height(8.dp))
        }


        item {
            HabitTypeChapter(
                currentHabitType = state.habitType,
                currentCountHabitNumber = state.habitCount,
                currentHour = state.habitTimedHour,
                currentMinute = state.habitTimedMinute,
                onHabitTypeClick = { onEvent(EditHabitEvent.OnHabitTypeChange(it)) },
                onPlusButtonClick = { onEvent(EditHabitEvent.OnIncreaseCount) },
                onMinusButtonClick = { onEvent(EditHabitEvent.OnDecreaseCount) },
                onSnappedHour = { onEvent(EditHabitEvent.OnHourTimedHabitChange(it)) },
                onSnappedMinute = { onEvent(EditHabitEvent.OnMinuteTimedHabitChange(it)) }
            )
            Spacer(Modifier.height(8.dp))
        }

        item {
            HabitFrequencyChapter(
                currentUIHabitFrequency = state.habitFrequency,
                onHabitFrequencyClick = {
                    onEvent(
                        EditHabitEvent.OnHabitFrequencyChange(
                            it
                        )
                    )
                },
                dailyExceptionDays = state.habitDailyExceptionDays.map { it.toStringFormat() },
                selectedWeeklyIndices = state.habitWeeklyDays,
                selectedMonthlyIndices = state.habitMonthlyDays,
                intervalHabitCurrentValue = state.habitIntervalXNumber,
                intervalHabitStartDay = state.habitIntervalStartDay,
                onAddDailyExceptionDay = {
                    onEvent(
                        EditHabitEvent.OnAddDailyHabitExceptionDay(
                            it
                        )
                    )
                },
                onRemoveDailyExceptionDay = {
                    onEvent(
                        EditHabitEvent.OnRemoveDailyHabitExceptionDay(
                            it
                        )
                    )
                },
                onToggleWeekDay = { onEvent(EditHabitEvent.OnToggleDayOfWeeklyHabit(it)) },
                onToggleMonthDay = {
                    onEvent(
                        EditHabitEvent.OnToggleDayOfMonthlyHabit(
                            it
                        )
                    )
                },
                onIntervalHabitSnappedValue = {
                    onEvent(
                        EditHabitEvent.OnIntervalHabitValueChange(
                            it
                        )
                    )
                },
                onIntervalHabitStartDayChange = {
                    onEvent(
                        EditHabitEvent.OnIntervalHabitStartDayChange(
                            it
                        )
                    )
                }
            )
            Spacer(Modifier.height(8.dp))
        }


        item {
            HabitRepetitionChapter(
                currentRepetition = state.habitRepetition,
                multipleHabitTimeList = state.multipleHabitTimes.map { it.toStringFormat() },
                onceHabitCurrentHour = state.onceTimeHabitHour,
                onceHabitCurrentMinute = state.onceTimeHabitMinute,
                reminderEnable = state.reminderEnabled,
                onRepetitionClick = {
                    onEvent(
                        EditHabitEvent.OnHabitRepetitionInDayChange(
                            it
                        )
                    )
                },
                onSnappedOnceHabitCurrentHour = {
                    onEvent(
                        EditHabitEvent.OnHourRepetitionInDayChange(
                            it
                        )
                    )
                },
                onSnappedOnceHabitCurrentMinute = {
                    onEvent(
                        EditHabitEvent.OnMinuteRepetitionInDayChange(
                            it
                        )
                    )
                },
                onMultipleHabitAddTime = {
                    onEvent(
                        EditHabitEvent.OnAddRepetitionTime(
                            it
                        )
                    )
                },
                onMultipleHabitRemoveTime = {
                    onEvent(
                        EditHabitEvent.OnRemoveRepetitionTime(
                            it
                        )
                    )
                },
                onReminderChange = {
                    onEvent(
                        EditHabitEvent.OnReminderEnableChange(
                            it,
                        )
                    )
                }
            )
            Spacer(Modifier.height(16.dp))
        }



        item {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (state.updateMode) {
                    UpdateAndDeleteButton(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary,
                        onUpdateClick = { onEvent(EditHabitEvent.OnSaveHabit) },
                        onDeleteClick = { onEvent(EditHabitEvent.OnDeleteHabit) }
                    )
                } else {
                    Button(
                        onClick = { onEvent(EditHabitEvent.OnSaveHabit) },
                        shape = StandardRoundedShape,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            stringResource(R.string.save),
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    }
                }
            }
        }
    }
}


//Screen Content/////////////////////////

/*

@Composable
private fun NewTaskHabitRepeatDaySelector(
    modifier: Modifier = Modifier,
    repeatDays: List<RepeatDayItemModel>,
    borderColor: Color,
    primaryColor: Color,
    onPrimaryColor: Color,
    onDayClick: (index: Int) -> Unit,
) {
    LazyRow {
        itemsIndexed(repeatDays) { index, repeatDay ->
            Box(
                modifier = modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        color = borderColor,
                        width = 1.dp,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(if (repeatDay.isSelected) primaryColor else onPrimaryColor)
                    .clickable {
                        onDayClick(index)
                    }
                    .padding(8.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = repeatDay.day,
                    textAlign = TextAlign.Center,
                    color = if (repeatDay.isSelected) onPrimaryColor else primaryColor,
                    fontWeight = if (repeatDay.isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun HabitTaskDropDownChooser(
    modifier: Modifier = Modifier,
    dropDownSelectedText: String,
    dropDownUnselectedText: String,
    dropDownTextColor: Color,
    onDropDownItemClick: (unselectedText: String) -> Unit,
    dropDowBackgroundColor: Color,
) {
    var isRotate by remember { mutableStateOf(false) }
    val rotate = animateFloatAsState(targetValue = if (isRotate) 180f else 0f)

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

        Box(
            modifier = Modifier
                .width(intrinsicSize = IntrinsicSize.Min)
                .clip(RoundedCornerShape(12.dp))
                .background(dropDowBackgroundColor),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier
                    .animateContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .width(120.dp)
                        .clickable { isRotate = !isRotate }
                ) {
                    Text(
                        text = dropDownSelectedText, color = dropDownTextColor,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    IconButton(onClick = { isRotate = !isRotate }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowDropDown,
                            contentDescription = "dropDown Icon",
                            tint = dropDownTextColor, modifier = Modifier.rotate(rotate.value)
                        )

                    }
                }

                if (isRotate)
                    Text(
                        text = dropDownUnselectedText,
                        color = dropDownTextColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp))
                            .clickable {
                                isRotate = false
                                onDropDownItemClick(
                                    dropDownUnselectedText
                                )
                            }
                            .padding(8.dp)
                    )
            }
        }

    }

}

@Composable
private fun NewTaskHabitExceptionDatesSelector(
    exceptionDays: List<String>,
    iconBackgroundColor: Color,
    contentColor: Color,
    borderColor: Color,
    onDatePickerResult: (date: PersianDate) -> Unit,
    onDeleteExceptionDate: (String) -> Unit,
) {
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    PersianWheelDatePickerDialog(showDatePicker = showDatePicker, onDismiss = {
        showDatePicker = false
    }) {
        showDatePicker = false
        onDatePickerResult(it)
    }
    Row {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .padding(top = 4.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor)
                .clickable { showDatePicker = true }
                .padding(8.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add icon",
                tint = contentColor
            )
        }
        val lazyListState = rememberLazyListState()
        LaunchedEffect(key1 = exceptionDays) {
            if (exceptionDays.isNotEmpty())
                lazyListState.animateScrollToItem(exceptionDays.lastIndex)
        }
        LazyRow(state = lazyListState) {
            items(exceptionDays) { exceptionDate ->
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(contentColor)
                        .border(
                            shape = RoundedCornerShape(12.dp),
                            color = borderColor,
                            width = 1.dp
                        )
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        onDeleteExceptionDate(exceptionDate)
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "Delete Icon",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    }
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = exceptionDate,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                }
            }
        }
    }
}

@Composable
private fun NewTaskHabitRepeatTypeSelector(
    borderColor: Color,
    selectedRepeatType: RepeatType,
    selectedBackgroundColor: Color,
    unselectedBackgroundColor: Color,
    selectedTextColor: Color,
    unselectedTextColor: Color,
    onRepeatTypeChange: (RepeatType) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RepeatType.entries.forEach {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        color = borderColor,
                        width = 1.dp,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(if (selectedRepeatType == it) selectedBackgroundColor else unselectedBackgroundColor)
                    .clickable {
                        onRepeatTypeChange(
                            it
                        )

                    }
                    .padding(8.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = it.stringResId),
                    textAlign = TextAlign.Center,
                    color = if (selectedRepeatType == it) selectedTextColor else unselectedTextColor,
                    fontWeight = if (selectedRepeatType == it) FontWeight.Bold else FontWeight.Normal
                )
            }

        }

    }
}

@Composable
private fun NewTaskHabitCheckBox(
    modifier: Modifier = Modifier,
    value: Boolean,
    text: String,
    textColor: Color,
    onValueChange: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(text = text, color = textColor)
        Checkbox(
            checked = value,
            onCheckedChange = {
                onValueChange(it)
            },
        )
    }
}


@Composable
fun TaskyTitleText(modifier: Modifier = Modifier, text: String, color: Color) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontSize = MaterialTheme.typography.titleMedium.fontSize
    )
}


@Composable
fun SaveButton(
    modifier: Modifier = Modifier, onButtonClick: () -> Unit, textColor: Color,
    backgroundColor: Color,
) {
    Button(
        onClick = { onButtonClick() },
        modifier = modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth(0.8f),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = textColor,
            containerColor = backgroundColor
        )
    ) {
        TaskyTitleText(
            text = stringResource(R.string.create),
            color = textColor,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun ButtonWithDeleteIcon(
    modifier: Modifier = Modifier,
    contentColor: Color,
    backgroundColor: Color,
    onDeleteButtonClick: () -> Unit,
    onUpdateButtonClick: () -> Unit,
) {

    val bColors = ButtonDefaults.buttonColors(
        contentColor = contentColor,
        containerColor = backgroundColor
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
            onClick = { onUpdateButtonClick() },
            modifier = Modifier
                .weight(8f)
                .fillMaxHeight(),
            colors = bColors
        ) {
            TaskyTitleText(
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
                    color = backgroundColor,
                    shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                )
                .clip(shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                .clickable {
                    onDeleteButtonClick()
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

private enum class DropDownChooserItems(
    @StringRes
    val stringResId: Int,
) {
    Habit(R.string.habit), Task(R.string.task)
}*/
