package af.amir.mytasky.presentation.home




import af.amir.mytasky.R
import af.amir.mytasky.presentation.commonComponent.EmptyState
import af.amir.mytasky.presentation.commonComponent.permission_handler.NotificationPermissionChecker
import af.amir.mytasky.presentation.home.component.TimeLineNode
import af.amir.mytasky.presentation.home.component.TimeLineNodePosition
import af.amir.mytasky.presentation.home.component.horizontal_calendar.HorizontalCalendar
import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.threeten.bp.LocalTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNewTaskHabitClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    var permissionChecker by remember { mutableStateOf(false) }
    if (permissionChecker){
        NotificationPermissionChecker {
            viewModel.onEvent(HomeScreenEvent.OnNotificationPermissionGranted(it))
            permissionChecker = false
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(uiState.scrollTargetIndex) {
        if (uiState.scrollTargetIndex != -1) {
            lazyListState.animateScrollToItem(index = uiState.scrollTargetIndex)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect{
            when(it){
                HomeScreenUiEffect.NotificationPermissionCheck -> permissionChecker = true
            }
        }
    }


    Scaffold(modifier = modifier
        .fillMaxSize()
        , floatingActionButton = {
        FloatingActionButton(onClick = { onNewTaskHabitClick() }) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add")
        }
    }) {

        HorizontalCalendar(onSelectedDate = {
            viewModel.onEvent(HomeScreenEvent.OnDateSelected(it))
        }) {
            if (uiState.groupedHabits.isEmpty()) {
                EmptyState(title = stringResource(R.string.habit_list_is_empty), subTitle = stringResource(R.string.your_habit_list_is_empty_for_this_day_click_on_button_and_add_your_first_habit))
            }else{

                Column( horizontalAlignment = Alignment.CenterHorizontally) {

                    if (uiState.isLoading) {
                        Box(
                            Modifier
                                .fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        val groupedHabits = uiState.groupedHabits.entries.toList()
                        val currentTime = LocalTime.now()
                        LazyColumn(
                            modifier = modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(
                                bottom = 100.dp,
                                top = 8.dp,
                                start = 8.dp,
                                end = 16.dp
                            ),
                            state = lazyListState
                        ) {
                            itemsIndexed(groupedHabits, key = { _, entry -> entry.key }) { index, entry ->
                                val time = entry.key
                                val habitsForTime = entry.value

                                val position = when (index) {
                                    0 -> TimeLineNodePosition.First
                                    groupedHabits.size - 1 -> TimeLineNodePosition.Last
                                    else -> TimeLineNodePosition.Middle
                                }

                                val isPast = time.isBefore(currentTime)


                                TimeLineNode (
                                    position = position,
                                    isPast = isPast,
                                    time = time,
                                    habits = habitsForTime,
                                    onHabitAction = { habit ->
                                        viewModel.onEvent(HomeScreenEvent.OnHabitItemClick(habit))
                                    }
                                )
                            }
                        }
                    }
                }
            }

        }


    }
}


