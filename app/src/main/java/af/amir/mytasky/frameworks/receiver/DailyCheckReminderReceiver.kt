package af.amir.mytasky.frameworks.receiver

import af.amir.mytasky.frameworks.managers.TaskyNotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class DailyCheckReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
       TaskyNotificationManager.showDailyCheckReminderNotification(context)
    }
}