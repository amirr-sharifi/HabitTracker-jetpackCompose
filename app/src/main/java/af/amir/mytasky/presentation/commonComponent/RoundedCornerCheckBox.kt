package af.amir.mytasky.presentation.commonComponent

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun RoundedCornerCheckbox(
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    size: Float = 24f,
    checkedColor: Color = MaterialTheme.colorScheme.primary,
    uncheckedColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    onValueChange: (Boolean) -> Unit,
) {
    val checkboxColor: Color by animateColorAsState(if (isChecked) checkedColor else uncheckedColor)
    val density = LocalDensity.current
    val duration = 800


    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color = checkboxColor, shape = CircleShape)
            .border(width = 1.5.dp, color = checkedColor, shape = CircleShape)
            .toggleable(
                value = isChecked,
                role = Role.Checkbox,
                onValueChange = onValueChange
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = isChecked,
            enter = slideInHorizontally(animationSpec = tween(duration)) {
                with(density) { (size * -0.5).dp.roundToPx() }
            } + expandHorizontally(
                expandFrom = Alignment.Start,
                animationSpec = tween(duration)
            ),
            exit = fadeOut()
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = uncheckedColor)
        }
    }

}