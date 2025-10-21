package af.amir.mytasky.presentation.commonComponent.persian_date_time_picker

import af.amir.mytasky.R
import af.amir.mytasky.presentation.commonComponent.dialog_scaffold.DialogScaffold
import af.amir.mytasky.presentation.commonComponent.persian_date_time_picker.core.WheelTimePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import org.threeten.bp.LocalTime

@Composable
fun PersianWheelTimePickerDialog(
    showDatePicker: Boolean,
    onDismiss: () -> Unit,
    onDoneClick: (LocalTime) -> Unit,
) {


    var snappedTime by remember {
        mutableStateOf(LocalTime.now())
    }

    DialogScaffold(
        showDialog = showDatePicker,
        onDismiss = onDismiss,
        titleText = stringResource(R.string.select_time),
        positiveButtonText = stringResource(R.string.select),
        negativeButtonText = stringResource(R.string.cancel),
        onDoneClick = {onDoneClick(snappedTime)}) {
        WheelTimePicker { time, _ ->
            snappedTime = time.snappedLocalTime
            time.snappedIndex
        }
    }

}