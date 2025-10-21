package af.amir.mytasky.presentation.home.component.horizontal_calendar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs


enum class SwipeDirection {
    END, START, NONE
}


@Composable
fun InteractivePager(
    modifier: Modifier = Modifier,
    threshold: Float = 0.4f, // Note: threshold should likely be positive
    iconSize: Dp = 64.dp,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    content: @Composable () -> Unit,
) {
    val offsetX = remember { Animatable(0f) }
    val swipeDirection = remember { mutableStateOf(SwipeDirection.NONE) }
    val isTriggered = remember { mutableStateOf(false) } // For haptic/bounce
    val shapeBounce = remember { Animatable(1f) }
    val haptic = LocalHapticFeedback.current
    val deviceDirection = LocalLayoutDirection.current

    val isThresholdExceeded = remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val iconSizePx = with(density) { iconSize.toPx() }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val screenWidth = constraints.maxWidth.toFloat()
        val thresholdPx = screenWidth * abs(threshold)

        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    coroutineScope {
                        detectDragGestures(
                            onDragStart = {
                                launch {
                                    offsetX.stop()
                                    shapeBounce.stop()
                                    shapeBounce.snapTo(1f)
                                    isTriggered.value = false
                                    isThresholdExceeded.value = false
                                }
                            },
                            onDrag = { change, amount ->
                                change.consume()
                                val normalizedAmountX = if (deviceDirection == LayoutDirection.Ltr) amount.x else -amount.x
                                if (swipeDirection.value == SwipeDirection.NONE) {
                                    swipeDirection.value =
                                        if (normalizedAmountX < 0) SwipeDirection.START else SwipeDirection.END
                                }

                                val currentOffset = offsetX.value
                                val newOffset = when (swipeDirection.value) {
                                    SwipeDirection.END -> (currentOffset + normalizedAmountX).coerceIn(
                                        0f,
                                        screenWidth
                                    )

                                    SwipeDirection.START -> (currentOffset + normalizedAmountX).coerceIn(
                                        -screenWidth,
                                        0f
                                    )

                                    SwipeDirection.NONE -> 0f
                                }
                                launch { offsetX.snapTo(newOffset) }

                                isThresholdExceeded.value = abs(newOffset) > thresholdPx

                                if (abs(newOffset) > iconSizePx && !isTriggered.value) {
                                    isTriggered.value = true
                                    launch {
                                        shapeBounce.animateTo(
                                            1.2f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMediumLow
                                            )
                                        )
                                        shapeBounce.animateTo(1f)
                                    }
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                } else if (abs(newOffset) < iconSizePx) {
                                    isTriggered.value = false
                                }
                            },

                            onDragEnd = {
                                if (isThresholdExceeded.value) {
                                    if (swipeDirection.value == SwipeDirection.END) onPrevious() else onNext()
                                }

                                launch {
                                    offsetX.animateTo(
                                        0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioNoBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    )
                                    isTriggered.value = false
                                    swipeDirection.value = SwipeDirection.NONE
                                    isThresholdExceeded.value = false
                                }

                            }
                        )
                    }
                }) {

            content()

                SwipeIndicator(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    iconSize = iconSize,
                    offsetX = offsetX.value,
                    shapeBounce = shapeBounce.value,
                    direction = SwipeDirection.START
                )

                SwipeIndicator(
                    modifier = Modifier.align(Alignment.CenterStart),
                    iconSize = iconSize,
                    offsetX = offsetX.value,
                    shapeBounce = shapeBounce.value,
                    direction = SwipeDirection.END
                )

        }
    }
}


@Composable
private fun SwipeIndicator(
    modifier: Modifier = Modifier,
    iconSize: Dp,
    offsetX: Float,
    shapeBounce: Float,
    direction: SwipeDirection,
) {
    val sign = if (direction == SwipeDirection.START) -1 else 1
    if ((sign * offsetX) <= 0) return

    val density = LocalDensity.current
    val iconSizePx = with(density) { iconSize.toPx() }
    val iconWidth = abs(offsetX).coerceAtMost(iconSizePx)
    val iconAlpha = ((iconWidth / iconSizePx) - 0.5f).coerceIn(0f, 1f) * 2
        Box(
            modifier
                .fillMaxHeight()
                .width(iconSize),
            contentAlignment = if (direction == SwipeDirection.START) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                Modifier
                    .size(
                        width = with(density) { iconWidth.toDp() },
                        height = iconSize
                    )
                    .graphicsLayer {
                        scaleX = shapeBounce
                        scaleY = shapeBounce
                        transformOrigin = if (direction == SwipeDirection.START)
                            TransformOrigin(1f, 0.5f) else
                            TransformOrigin(0f, 0.5f)
                    }
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (direction == SwipeDirection.START) Icons.AutoMirrored.Filled.KeyboardArrowRight else Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary.copy(alpha = iconAlpha),

                    )
            }
        }

}