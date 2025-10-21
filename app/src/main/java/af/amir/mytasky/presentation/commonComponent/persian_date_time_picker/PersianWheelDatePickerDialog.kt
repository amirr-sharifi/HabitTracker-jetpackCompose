package af.amir.mytasky.presentation.commonComponent.persian_date_time_picker

import af.amir.mytasky.R
import af.amir.mytasky.presentation.commonComponent.dialog_scaffold.DialogScaffold
import af.amir.mytasky.presentation.commonComponent.persian_date_time_picker.core.WheelDatePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import saman.zamani.persiandate.PersianDate

@Composable
fun PersianWheelDatePickerDialog(
    showDatePicker: Boolean,
    onDismiss: () -> Unit,
    onDoneClick: (PersianDate) -> Unit,
) {
    var snappedTime by remember {
        mutableStateOf(PersianDate())
    }
    DialogScaffold(
        showDialog = showDatePicker,
        onDismiss = onDismiss,
        titleText = stringResource(R.string.select_date),
        positiveButtonText = stringResource(R.string.select),
        negativeButtonText = stringResource(R.string.cancel),
        onDoneClick = { onDoneClick(snappedTime) }) {
        WheelDatePicker { date ->
            snappedTime = date.snappedPersianDate
            date.snappedIndex
        }
    }
}