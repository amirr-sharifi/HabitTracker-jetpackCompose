package af.amir.mytasky.presentation.home.component.items

import af.amir.mytasky.domain.model.DailyHabitType
import af.amir.mytasky.presentation.model.CountableHabitUiModel
import af.amir.mytasky.presentation.model.DailyHabitUiModel
import af.amir.mytasky.presentation.model.SimpleHabitUiModel
import af.amir.mytasky.presentation.model.TimedHabitUiModel
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun HabitActionButton(
    modifier: Modifier = Modifier,
    dailyHabit: DailyHabitUiModel,
    onAction: () -> Unit,
    size: Dp = 48.dp,
    cornerRadius: Dp = 12.dp,
    colorIdle: Color = MaterialTheme.colorScheme.surfaceVariant,
    colorActive: Color = Color(0xff4CAF50),
) {

    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    var animating by remember { mutableStateOf(false) }

    val tickProgress = remember { Animatable(0f) }
    val state = dailyHabit.toHabitActionState()
    LaunchedEffect(dailyHabit) {
        if (state == HabitActionState.Complete) {
            tickProgress.snapTo(0f)
            tickProgress.animateTo(1f, tween(400, easing = LinearEasing))
        }
    }

    val click = let@{
        if (animating) return@let
        animating = true
        scope.launch {
            scale.animateTo(0.85f, tween(90))
            onAction.invoke()
            scale.animateTo(
                1f, spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
        animating = false
    }

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleY = scale.value
                scaleX = scale.value
            }
            .clip(RoundedCornerShape(cornerRadius))
            .background(colorIdle)
            .clickable { click() },
        contentAlignment = Alignment.Center
    ) {
        if (state == HabitActionState.Complete) {
            DrawAnimatedTick(
                progress = tickProgress.value,
                color = colorActive,
                strokeWidth = size.value * 0.12f
            )
        } else {
            Icon(
                imageVector = when (dailyHabit) {
                    is SimpleHabitUiModel -> Icons.Rounded.RadioButtonUnchecked
                    is TimedHabitUiModel ->if (state == HabitActionState.Idle) Icons.Rounded.PlayArrow else Icons.Rounded.Pause
                    is CountableHabitUiModel -> Icons.Rounded.Add
                }, null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(size * 0.6f)
            )
        }


    }
}


@Composable
private fun DrawAnimatedTick(progress: Float, color: Color, strokeWidth: Float) {

    Canvas(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {

        val path = Path().apply {
            val w = size.width
            val h = size.height
            moveTo(w * 0.25f, h * 0.5f)
            lineTo(w * 0.45f, h * 0.7f)
            lineTo(w * 0.75f, h * 0.3f)
        }

        val measure = PathMeasure()
        measure.setPath(path, false)
        val dst = Path()
        measure.getSegment(0f, measure.length * progress, dst, true)
        drawPath(
            path = dst,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }

}