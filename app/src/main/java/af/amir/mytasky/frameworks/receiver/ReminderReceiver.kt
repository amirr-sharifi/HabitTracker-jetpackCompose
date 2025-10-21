package af.amir.mytasky.frameworks.receiver

import af.amir.mytasky.domain.repository.HabitRepository
import af.amir.mytasky.frameworks.managers.TaskyNotificationManager
import af.amir.mytasky.frameworks.managers.ReminderManager
import af.amir.mytasky.frameworks.util.ReminderConstant
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {


    @Inject
    lateinit var habitRepository: HabitRepository

    @Inject
    lateinit var reminderManager: ReminderManager

    override fun onReceive(context: Context, intent: Intent?) {

        val id = intent?.getLongExtra(ReminderConstant.REMINDER_ID_EXTRA_KEY, -1)

        val scope = CoroutineScope(IO)
        scope.launch {

            val habit = habitRepository.getHabitById(id ?: -1)
            habit?.let {
                TaskyNotificationManager.showNotification(context, it.title, it.description)
                reminderManager.setHabitReminder(it)
            }


        }
    }
}