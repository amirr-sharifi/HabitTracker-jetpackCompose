package af.amir.mytasky.frameworks.receiver

import af.amir.mytasky.data.local.prefrences.TaskyDataStorePreferencesManager
import af.amir.mytasky.domain.repository.HabitRepository
import af.amir.mytasky.frameworks.managers.ReminderManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompleteReceiver : BroadcastReceiver() {


    @Inject lateinit var habitRepository: HabitRepository

    @Inject
    lateinit var reminderManager: ReminderManager

    @Inject
    lateinit var preferencesManager: TaskyDataStorePreferencesManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            CoroutineScope(IO).launch {
                habitRepository.getHabits().collectLatest {
                    it.forEach { habit ->
                        if (habit.reminderEnabled)
                            reminderManager.setHabitReminder(habit)
                    }
                }
            }

            CoroutineScope(IO).launch {
                preferencesManager.getDailyCheckReminderInfo { time, enabled ->
                    if (enabled)
                        reminderManager.setDailyCheckAlarm(time)
                }
            }
        }
    }
}