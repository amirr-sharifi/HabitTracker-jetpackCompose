package af.amir.mytasky.presentation.home.component.items

import af.amir.mytasky.R
import af.amir.mytasky.domain.model.DailyHabitTimerStatus
import af.amir.mytasky.presentation.model.CountableHabitUiModel
import af.amir.mytasky.presentation.model.DailyHabitUiModel
import af.amir.mytasky.presentation.model.SimpleHabitUiModel
import af.amir.mytasky.presentation.model.TimedHabitUiModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun DailyHabitUiItem(
    modifier: Modifier = Modifier,
    dailyHabit: DailyHabitUiModel,
    onAction: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    shape: Shape = MaterialTheme.shapes.medium,
) {

    val textDecoration =
        remember(dailyHabit) {
            when (dailyHabit) {
                is CountableHabitUiModel -> if (dailyHabit.goal == dailyHabit.current) TextDecoration.LineThrough else TextDecoration.None
                is SimpleHabitUiModel -> if (dailyHabit.isDone) TextDecoration.LineThrough else TextDecoration.None
                is TimedHabitUiModel -> if (dailyHabit.timerStatus == DailyHabitTimerStatus.Complete) TextDecoration.LineThrough else TextDecoration.None
            }
        }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, shape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(0.9f)
        ) {
            Text(
                text = dailyHabit.title,
                textDecoration = textDecoration,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = when (dailyHabit) {
                    is CountableHabitUiModel -> "${dailyHabit.current} / ${dailyHabit.goal}"
                    is SimpleHabitUiModel -> if(dailyHabit.description.isNullOrEmpty()) stringResource(R.string.no_description_available) else dailyHabit.description!!
                    is TimedHabitUiModel -> "${dailyHabit.currentTime} / ${dailyHabit.goalTime}"
                },
                fontWeight = FontWeight.Normal,
                fontSize = if (dailyHabit is SimpleHabitUiModel && dailyHabit.description.isNullOrEmpty())
                    MaterialTheme.typography.bodySmall.fontSize
                else
                    MaterialTheme.typography.bodyMedium.fontSize,
                maxLines = 1,
                color = if (dailyHabit is SimpleHabitUiModel && dailyHabit.description.isNullOrEmpty())
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                else
                    MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(textDirection = TextDirection.Ltr),
            )
        }


        HabitActionButton(dailyHabit = dailyHabit, onAction = onAction)


    }


}