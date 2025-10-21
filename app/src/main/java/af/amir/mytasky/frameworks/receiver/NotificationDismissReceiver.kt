package af.amir.mytasky.frameworks.receiver

import af.amir.mytasky.frameworks.util.ReminderConstant
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

@AndroidEntryPoint
class NotificationDismissReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val notificationId = intent.getIntExtra(ReminderConstant.NOTIFICATION_ID_EXTRA_KEY, -1)
        action?.let {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(notificationId)
        }
    }
}