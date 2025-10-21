package af.amir.mytasky.presentation.home.component.horizontal_calendar

import af.amir.mytasky.R
import af.amir.mytasky.presentation.commonComponent.persian_date_time_picker.PersianWheelDatePickerDialog
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SwipeLeftAlt
import androidx.compose.material.icons.rounded.SwipeRightAlt
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import saman.zamani.persiandate.PersianDate
import kotlin.math.abs

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HorizontalCalendar(
    modifier: Modifier = Modifier,
    calendarViewModel: CalendarViewModel = hiltViewModel(),
    onSelectedDate: (PersianDate) -> Unit,
    content: @Composable () -> Unit,
) {
    val uiState by calendarViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        calendarViewModel.uiEffects.collect { effect ->
            when (effect) {
                is CalendarUiEffects.SelectedDayChange -> {
                    onSelectedDate(effect.date)
                }
            }
        }
    }






    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        CalendarHeader(
            monthName = uiState.monthName,
            selectedDayIsToday = uiState.selectedDayIsToday,
            onEvents = calendarViewModel::onEvents,
        )
        CalendarDaysList(
            modifier = Modifier.padding(bottom = 8.dp),
            dayList = uiState.currentMonthDayList,
            selectedDatPosition = uiState.selectedDayIndex,
        ) {
            calendarViewModel.onEvents(HorizontalCalendarEvents.OnDayClick(it))
        }

        InteractivePager(
            onNext = { calendarViewModel.onEvents(HorizontalCalendarEvents.OnAddDay) },
            onPrevious = { calendarViewModel.onEvents(HorizontalCalendarEvents.OnSubDay) }) {
            content()
        }
    }
}

