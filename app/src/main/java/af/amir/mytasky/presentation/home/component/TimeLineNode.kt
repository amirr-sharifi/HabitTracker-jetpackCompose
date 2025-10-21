package af.amir.mytasky.presentation.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import af.amir.mytasky.presentation.home.component.items.DailyHabitUiItem
import af.amir.mytasky.presentation.model.DailyHabitUiModel
import af.amir.mytasky.util.toStringFormat
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun TimeLineNode(
    position: TimeLineNodePosition,
    isPast: Boolean,
    time: LocalTime,
    habits: List<DailyHabitUiModel>,
    onHabitAction: (DailyHabitUiModel) -> Unit
) {
    var circleCenterY by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val canvasWidth  = size.width
                val canvasHeight = size.height
                val timelineX = with(density) { canvasWidth - 16.dp.toPx() }
                val circleRadiusPx = with(density) { 8.dp.toPx() }
                val strokeWidthPx = with(density) { 2.dp.toPx() }
                val lineColor = Color.LightGray

                if (position != TimeLineNodePosition.First) {
                    drawLine(
                        color = lineColor,
                        start = Offset(x = timelineX, y = 0f),
                        end = Offset(x = timelineX, y = circleCenterY - circleRadiusPx),
                        strokeWidth = strokeWidthPx
                    )
                }

                if (position != TimeLineNodePosition.Last) {
                    drawLine(
                        color = lineColor,
                        start = Offset(x = timelineX, y = circleCenterY + circleRadiusPx),
                        end = Offset(x = timelineX, y = canvasHeight),
                        strokeWidth = strokeWidthPx
                    )
                }

                if (circleCenterY > 0f) {
                    val circleCenter = Offset(timelineX, circleCenterY)
                    if (isPast) {
                        drawCircle(color = lineColor, radius = circleRadiusPx, center = circleCenter)
                    } else {
                        drawCircle(color = lineColor, radius = circleRadiusPx, center = circleCenter, style = Stroke(width = strokeWidthPx))
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.width(32.dp))

                Column {
                    Text(
                        text = time.toStringFormat(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                        modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                            circleCenterY = layoutCoordinates.size.height / 2f + layoutCoordinates.positionInParent().y
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    habits.forEach { habit ->
                        key(habit.id){
                            DailyHabitUiItem(
                                dailyHabit = habit,
                                onAction = { onHabitAction(habit) }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

enum class TimeLineNodePosition {
    First, Middle, Last
}