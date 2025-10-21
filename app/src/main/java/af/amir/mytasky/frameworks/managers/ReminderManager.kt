package af.amir.mytasky.frameworks.managers

import af.amir.mytasky.domain.model.Habit
import af.amir.mytasky.domain.model.HabitFrequency
import af.amir.mytasky.domain.model.HabitRepetition
import af.amir.mytasky.frameworks.receiver.DailyCheckReminderReceiver
import af.amir.mytasky.frameworks.receiver.ReminderReceiver
import af.amir.mytasky.frameworks.util.ReminderConstant
import af.amir.mytasky.util.isDayBefore
import af.amir.mytasky.util.toEpochDay
import af.amir.mytasky.util.toLocalDate
import af.amir.mytasky.util.toLocalTime
import af.amir.mytasky.util.toPersianDate
import af.amir.mytasky.util.toStringFormat
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import org.threeten.bp.LocalTime
import saman.zamani.persiandate.PersianDate
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderManager @Inject constructor(
    @ApplicationContext
    val context: Context,
) {

    private val TAG = "Alarm"
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private val dailyCheckRequestCode = 12123

    fun askAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() == false) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    context.startActivity(intent)
                }
            }
        }
    }

    fun setHabitReminder(habit: Habit) {

        if (!habit.reminderEnabled) {
            cancelAlarm(habit = habit)
            return
        }

        val nextAlarmTimeInMillis = findNextAlarmTimeInMillis(habit)

        if (nextAlarmTimeInMillis != null) {
            setAlarm(timeInMillis = nextAlarmTimeInMillis, habit = habit)
        } else {
            cancelAlarm(habit)
        }
    }

    private fun findNextAlarmTimeInMillis(habit: Habit): Long? {
        val now = PersianDate.today()
        val currentTime = LocalTime.now()

        val sortedTimes = when (val repetition = habit.repetition) {
            is HabitRepetition.Multiple -> repetition.times.map { it.time.toLocalTime() }.sorted()
            is HabitRepetition.Once -> listOf(repetition.time.time.toLocalTime())
        }

        if (sortedTimes.isEmpty()) return null

        if (isRelevantToday(habit, now)) {
            Log.e(TAG, "$sortedTimes")
            Log.e(TAG, "$currentTime")
            val nextTimeInDay =
                sortedTimes.firstOrNull { currentTime.isBefore(it) }
            if (nextTimeInDay != null) {
                Log.e(TAG, "findNextAlarmTimeInMillis: Nowwwwww")
                return combineDateAndTime(now, nextTimeInDay)
            }
        }

        val searchDate = PersianDate.today().apply { addDay() }

        for (i in 0..365) {
            if (isRelevantToday(habit, searchDate)) {
                val nextTimeInDay = sortedTimes.first()
                return combineDateAndTime(searchDate, nextTimeInDay)
            }
            searchDate.addDay()
        }


        return null
    }

    private fun combineDateAndTime(now: PersianDate, nextTimeInDay: LocalTime): Long {
        val calendar = Calendar.getInstance().apply {
            time = now.toDate()
            set(Calendar.HOUR_OF_DAY, nextTimeInDay.hour)
            set(Calendar.MINUTE, nextTimeInDay.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }


    private fun isRelevantToday(habit: Habit, date: PersianDate): Boolean {
        val createAt = habit.createdAt.date.toPersianDate()
        if (date.isDayBefore(createAt)) return false

        return when (val frequency = habit.frequency) {
            is HabitFrequency.Daily -> date.toStringFormat() !in frequency.exceptionDays.map { it.date }
            is HabitFrequency.Weekly -> date.toLocalDate().dayOfWeek.value % 7 in frequency.daysInWeeks
            is HabitFrequency.Monthly -> (date.shDay - 1) in frequency.daysInMonth
            is HabitFrequency.Interval -> {
                val startDateEpoch = frequency.echoDay.date.toPersianDate().toEpochDay()
                val currentDateEpoch = date.toEpochDay()
                (currentDateEpoch - startDateEpoch) % frequency.everyXDay == 0L
            }
        }
    }




    private fun cancelAlarm(habit: Habit? = null) {
        Log.e(TAG, "cancelAlarm")
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habit!!.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun setAlarm(
        timeInMillis: Long,
        habit: Habit? = null,
    ) {

        val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra(ReminderConstant.REMINDER_ID_EXTRA_KEY, habit!!.id)

        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
             habit!!.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )

        Log.e(TAG, "set alarm at time : ${Date(timeInMillis)}")
    }

    fun setDailyCheckAlarm(time: LocalTime) {
        val intent = Intent(context, DailyCheckReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            dailyCheckRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()
        val currentTime = LocalTime.now()
        if (currentTime.isAfter(time))
            calendar.add(Calendar.DAY_OF_YEAR, 1)

        calendar.apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Log.e(TAG, "set alarm at time : ${Date(calendar.timeInMillis)}")
    }


    fun cancelDailyCheckAlarm() {
        Log.e(TAG, "cancelAlarm")
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            dailyCheckRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

}