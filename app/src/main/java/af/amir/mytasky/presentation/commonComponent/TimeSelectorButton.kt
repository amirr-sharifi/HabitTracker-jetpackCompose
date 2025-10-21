package af.amir.mytasky.presentation.commonComponent

import af.amir.mytasky.presentation.commonComponent.persian_date_time_picker.PersianWheelTimePickerDialog
import af.amir.mytasky.util.toStringFormat
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.threeten.bp.LocalTime

@Composable
fun TimeSelectorButton(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp,
    selectedTime: LocalTime,
    textColor: Color,
    borderColor: Color,
    enabled: Boolean = true,
    shape: Shape =RoundedCornerShape(12.dp),
    onTimePickerResult: (LocalTime) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    PersianWheelTimePickerDialog(showDatePicker = showDialog, onDismiss = { showDialog = false }) {
        onTimePickerResult(it)
        showDialog = false
    }

    Box(
        modifier = modifier
            .padding(end = horizontalPadding)
            .clip(shape)
            .border(
                color = borderColor,
                shape = shape,
                width = 1.dp
            )
            .clickable {
                if (enabled)
                    showDialog = true
            }
            .padding(8.dp), contentAlignment = Alignment.Center
    ) { Text(text = selectedTime.toStringFormat(), color = textColor) }


}