package af.amir.mytasky.presentation.commonComponent.persian_date_time_picker.core

import af.amir.mytasky.util.getMonthNameForDevice
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import saman.zamani.persiandate.PersianDate

@Composable
internal fun WheelDatePicker(
    modifier: Modifier = Modifier,
    startDate: PersianDate = PersianDate(),
    yearsRange: IntRange? = IntRange(1350, 1450),
    size: DpSize = DpSize(256.dp, 256.dp),
    rowCount: Int = 5,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    textColor: Color = LocalContentColor.current,
    selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
    onSnappedDate: (snappedDate: SnappedDate) -> Int? = { _ -> null },
) {
    var snappedDate by remember { mutableStateOf(startDate) }

    var dayOfMonths by remember {
        mutableStateOf(calculateDayOfMonths(snappedDate.shMonth, snappedDate.shYear))
    }

    val months = (1..12).map {
        val pDate = PersianDate().apply {
            shMonth = it
        }
        Month(
            text = if (size.width / 3 < 55.dp) {
                pDate.shMonth.toString()
            } else pDate.getMonthNameForDevice(),
            value = it,
            index = it - 1
        )
    }

    val years = yearsRange?.map {
        Year(
            text = it.toString(),
            value = it,
            index = yearsRange.indexOf(it)
        )
    }


    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (selectorProperties.enabled().value) {
            Surface(
                modifier = Modifier
                    .size(size.width, size.height / rowCount),
                shape = selectorProperties.shape().value,
                color = selectorProperties.color().value,
                border = selectorProperties.border().value
            ) {}
        }
        Row {
            //Day of Month
            WheelTextPicker(
                size = DpSize(
                    width = if (yearsRange == null) size.width / 2 else size.width / 3,
                    height = size.height
                ),
                texts = dayOfMonths.map { it.text },
                rowCount = rowCount,
                style = textStyle,
                color = textColor,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    enabled = false
                ),
                startIndex = dayOfMonths.find { it.value == startDate.shDay }?.index ?: 0,
                onScrollFinished = { snappedIndex ->
                    val newDayOfMonth = dayOfMonths.find { it.index == snappedIndex }?.value
                    newDayOfMonth?.let {
                        val newDate = snappedDate.apply {
                            shDay = newDayOfMonth
                        }
                        snappedDate = newDate

                        val newIndex = dayOfMonths.find { it.value == snappedDate.shDay }?.index

                        newIndex?.let {
                            onSnappedDate(
                                SnappedDate.DayOfMonth(
                                    localDate = snappedDate,
                                    index = newIndex
                                )
                            )?.let { return@WheelTextPicker it }
                        }
                    }

                    return@WheelTextPicker dayOfMonths.find { it.value == snappedDate.shDay }?.index
                }
            )
            //Month
            WheelTextPicker(
                size = DpSize(
                    width = if (yearsRange == null) size.width / 2 else size.width / 3,
                    height = size.height
                ),
                texts = months.map { it.text },
                rowCount = rowCount,
                style = textStyle,
                color = textColor,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                    enabled = false
                ),
                startIndex = months.find { it.value == startDate.shMonth }?.index ?: 0,
                onScrollFinished = { snappedIndex ->
                    val newMonth = months.find { it.index == snappedIndex }?.value
                    newMonth?.let {
                        val newDate = snappedDate.apply {
                            shMonth = it
                        }
                        snappedDate = newDate
                        dayOfMonths = calculateDayOfMonths(snappedDate.shMonth, snappedDate.shYear)
                        val newIndex = months.find { it.value == snappedDate.shMonth }?.index
                        newIndex?.let {
                            onSnappedDate(
                                SnappedDate.Month(
                                    localDate = snappedDate,
                                    index = newIndex
                                )
                            )?.let { return@WheelTextPicker it }
                        }
                    }


                    return@WheelTextPicker months.find { it.value == snappedDate.shMonth }?.index
                }
            )
            //Year
            years?.let { years ->
                WheelTextPicker(
                    size = DpSize(
                        width = size.width / 3,
                        height = size.height
                    ),
                    texts = years.map { it.text },
                    rowCount = rowCount,
                    style = textStyle,
                    color = textColor,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    startIndex = years.find { it.value == startDate.shYear }?.index ?: 0,
                    onScrollFinished = { snappedIndex ->

                        val newYear = years.find { it.index == snappedIndex }?.value

                        newYear?.let {

                            val newDate = snappedDate.apply {
                                shYear = it
                            }

                            snappedDate = newDate


                            dayOfMonths =
                                calculateDayOfMonths(snappedDate.shMonth, snappedDate.shYear)

                            val newIndex = years.find { it.value == snappedDate.shYear }?.index

                            newIndex?.let {
                                onSnappedDate(
                                    SnappedDate.Year(
                                        localDate = snappedDate,
                                        index = newIndex
                                    )
                                )?.let { return@WheelTextPicker it }

                            }
                        }

                        return@WheelTextPicker years.find { it.value == snappedDate.shYear }?.index
                    }
                )
            }
        }
    }


}

internal data class DayOfMonth(
    val text: String,
    val value: Int,
    val index: Int,
)

private data class Month(
    val text: String,
    val value: Int,
    val index: Int,
)

private data class Year(
    val text: String,
    val value: Int,
    val index: Int,
)

internal fun calculateDayOfMonths(month: Int, year: Int): List<DayOfMonth> {

    val isLeapYear = PersianDate().apply {
        shMonth = month
        shYear = year
    }.isLeap

    val month31day = (1..31).map {
        DayOfMonth(
            text = it.toString(),
            value = it,
            index = it - 1
        )
    }
    val month30day = (1..30).map {
        DayOfMonth(
            text = it.toString(),
            value = it,
            index = it - 1
        )
    }
    val month29day = (1..29).map {
        DayOfMonth(
            text = it.toString(),
            value = it,
            index = it - 1
        )
    }

    return when (month) {
        1 -> {
            month31day
        }

        2 -> {
            month31day
        }

        3 -> {
            month31day
        }

        4 -> {
            month31day
        }

        5 -> {
            month31day
        }

        6 -> {
            month31day
        }

        7 -> {
            month30day
        }

        8 -> {
            month30day
        }

        9 -> {
            month30day
        }

        10 -> {
            month30day
        }

        11 -> {
            month30day
        }

        12 -> {
            if (isLeapYear) month29day else month30day
        }

        else -> {
            emptyList()
        }
    }
}