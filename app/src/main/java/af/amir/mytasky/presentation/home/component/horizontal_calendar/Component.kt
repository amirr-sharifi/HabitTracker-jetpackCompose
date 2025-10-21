package af.amir.mytasky.presentation.home.component.horizontal_calendar

import af.amir.mytasky.R
import af.amir.mytasky.presentation.commonComponent.persian_date_time_picker.PersianWheelDatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun CalendarHeader(
    modifier: Modifier = Modifier,
    monthName: String,
    selectedDayIsToday: Boolean,
    onEvents: (HorizontalCalendarEvents) -> Unit,
) {
    var showDatePicker by remember {
        mutableStateOf(false)
    }
    PersianWheelDatePickerDialog(showDatePicker = showDatePicker, onDismiss = {
        showDatePicker = false
    }) {
        showDatePicker = false
        onEvents(HorizontalCalendarEvents.OnPickDayFromDialog(it))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(8.dp)
            .clip(shape = RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = stringResource(R.string.next_month),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(12.dp))
                    .clickable {
                        onEvents(HorizontalCalendarEvents.OnMonthChange(1))
                    }
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            androidx.compose.animation.AnimatedVisibility(visible = !selectedDayIsToday) {
                IconButton(
                    onClick = { onEvents(HorizontalCalendarEvents.OnBackToday()) },
                ) {
                    Icon(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        painter = painterResource(id = R.drawable.ic_back_today),
                        contentDescription = "Arrow Left", tint = Color.Gray
                    )
                }
            }
        }


        Row(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(12.dp))
                .clickable { showDatePicker = true }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = monthName, style = TextStyle(
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )


            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Rounded.ArrowDropDown,
                contentDescription = "Arrow Down", tint = Color.LightGray
            )


        }

        Text(
            text = stringResource(R.string.previous_month),
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier
                .clip(shape = RoundedCornerShape(12.dp))
                .clickable {
                    onEvents(HorizontalCalendarEvents.OnMonthChange(-1))
                }
                .padding(4.dp)
        )

    }
}

@Composable
fun CalendarDaysList(
    modifier: Modifier = Modifier,
    dayList: List<UiDay>,
    selectedDatPosition: Int,
    onDayClick: (index: Int) -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = selectedDatPosition) {
        if (selectedDatPosition >= 0) {
            if (selectedDatPosition >= 2)
                listState.animateScrollToItem(selectedDatPosition - 2)
            else
                listState.animateScrollToItem(selectedDatPosition)
        } else
            listState.animateScrollToItem(0)
    }
    LazyRow(modifier = modifier.fillMaxWidth(), state = listState) {
        items(dayList.size) { index ->
            CalendarDayItem(day = dayList[index], isSelected = index == selectedDatPosition) {
                onDayClick(index)
            }
        }
    }
}

@Composable
fun CalendarDayItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    day: UiDay,
    onDayClick: () -> Unit,
) {

    val backgroundColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
    val contentColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .padding(horizontal = 6.dp)
            .height(80.dp)
            .width(60.dp)
            .clip(shape = RoundedCornerShape(12.dp))
            .clickable { onDayClick() }
            .background(backgroundColor)
            .padding(4.dp), contentAlignment = Alignment.Center

    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

            Text(
                text = day.dayOfMonth.toString(),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    textAlign = TextAlign.Center,
                    color = contentColor,
                    fontWeight = fontWeight
                )
            )


            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = day.dayOfWeekName,
                maxLines = 1,
                style = TextStyle(
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    textAlign = TextAlign.Center,
                    color = contentColor,
                    fontWeight = fontWeight
                )
            )

            if (day.isToday)
                Box(
                    Modifier
                        .fillMaxWidth(0.6f)
                        .height(8.dp)
                        .background(contentColor, RoundedCornerShape(12.dp))
                )

        }
    }
}
