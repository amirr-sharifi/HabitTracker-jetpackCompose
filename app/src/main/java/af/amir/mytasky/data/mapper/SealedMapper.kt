package af.amir.mytasky.data.mapper

import af.amir.mytasky.domain.model.DailyHabitTimerStatus
import af.amir.mytasky.domain.model.DailyHabitType
import af.amir.mytasky.domain.model.HabitDate
import af.amir.mytasky.domain.model.HabitFrequency
import af.amir.mytasky.domain.model.HabitRepetition
import af.amir.mytasky.domain.model.HabitTime
import af.amir.mytasky.domain.model.HabitType

private const val SIMPLE_HABIT = "simple"
private const val COUNTER_HABIT = "counter"
private const val TIMER_HABIT = "timer"

const val DAILY_FREQUENCY = "daily"
const val WEEKLY_FREQUENCY = "weekly"
const val MONTHLY_FREQUENCY = "monthly"
const val INTERVAL_FREQUENCY = "interval"

private const val ONCE_HABIT = "once"
private const val MULTIPLE_HABIT = "multiple"


fun HabitType.toDbString(): String = when (this) {
    HabitType.Simple -> SIMPLE_HABIT
    is HabitType.Countable -> "$COUNTER_HABIT:${goal}"
    is HabitType.Timed -> "$TIMER_HABIT:${goal}"
}

fun String.toHabitType(): HabitType = when {
    this == SIMPLE_HABIT -> HabitType.Simple
    this.startsWith(COUNTER_HABIT) -> HabitType.Countable(substringAfter("$COUNTER_HABIT:").toInt())
    this.startsWith(TIMER_HABIT) -> HabitType.Timed(substringAfter("$TIMER_HABIT:").toLong())
    else -> HabitType.Simple
}


fun HabitFrequency.toDbString(): String = when (this) {
    is HabitFrequency.Daily -> "$DAILY_FREQUENCY:${exceptionDays.joinToString(",") { it.date }}"
    is HabitFrequency.Monthly -> "$MONTHLY_FREQUENCY:${daysInMonth.joinToString(",")}"
    is HabitFrequency.Weekly -> "$WEEKLY_FREQUENCY:${daysInWeeks.joinToString(",")}"
    is HabitFrequency.Interval -> "$INTERVAL_FREQUENCY:${everyXDay}:${echoDay.date}"
}

fun String.toHabitFrequency(): HabitFrequency = when {
    startsWith(DAILY_FREQUENCY) -> {
        val exceptionsString = substringAfter("$DAILY_FREQUENCY:")
        if (exceptionsString.isEmpty())
            HabitFrequency.Daily(emptyList())
        else
            HabitFrequency.Daily(exceptionsString.split(",").map { HabitDate(it) })
    }

    startsWith(WEEKLY_FREQUENCY) -> HabitFrequency.Weekly(
        substringAfter("$WEEKLY_FREQUENCY:").split(",").map { it.toInt() })

    startsWith(MONTHLY_FREQUENCY) -> HabitFrequency.Monthly(
        substringAfter("$MONTHLY_FREQUENCY:").split(",").map { it.toInt() })

    startsWith(INTERVAL_FREQUENCY) -> {
        val parts = substringAfter("$INTERVAL_FREQUENCY:").split(":")
        val everyXDay = parts[0]
        val echoDay = parts[1]
        HabitFrequency.Interval(everyXDay.toInt(), HabitDate(echoDay))
    }

    else -> HabitFrequency.Daily(emptyList())
}

fun HabitRepetition.toDbString(): String = when (this) {
    is HabitRepetition.Once -> "$ONCE_HABIT:${time.time}"
    is HabitRepetition.Multiple -> "$MULTIPLE_HABIT:${times.joinToString(",") { it.time }}"
}

fun String.toHabitRepetition(): HabitRepetition = when {
    startsWith(MULTIPLE_HABIT) -> HabitRepetition.Multiple(
        substringAfter("$MULTIPLE_HABIT:").split(",")
            .map { HabitTime(it) })

    else -> {
        val stringTime = substringAfter("$ONCE_HABIT:")
        val time = HabitTime(stringTime)
        HabitRepetition.Once(time)
    }
}


private const val SIMPLE_DAILY_HABIT = "Dsimple"
private const val COUNTER_DAILY_HABIT = "Dcounter"
private const val TIMER_DAILY_HABIT = "Dtimer"

fun DailyHabitType.toDbString(): String = when (this) {
    is DailyHabitType.Simple -> SIMPLE_DAILY_HABIT
    is DailyHabitType.Countable -> COUNTER_DAILY_HABIT
    is DailyHabitType.Timed -> TIMER_DAILY_HABIT
}

fun String.toDailyHabitType(): DailyHabitType = when {
    this == SIMPLE_DAILY_HABIT -> DailyHabitType.Simple(false)
    this.startsWith(COUNTER_DAILY_HABIT) -> DailyHabitType.Countable(0,0)
    this.startsWith(TIMER_DAILY_HABIT) -> DailyHabitType.Timed(DailyHabitTimerStatus.Idle,0L,0L)
    else -> DailyHabitType.Simple(false)
}
