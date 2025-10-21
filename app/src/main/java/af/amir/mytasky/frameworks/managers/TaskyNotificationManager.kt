package af.amir.mytasky.frameworks.managers

import af.amir.mytasky.MainActivity
import af.amir.mytasky.R
import af.amir.mytasky.frameworks.receiver.NotificationDismissReceiver
import af.amir.mytasky.frameworks.util.ReminderConstant.NOTIFICATION_CHANNEL_ID
import af.amir.mytasky.frameworks.util.ReminderConstant.NOTIFICATION_CHANNEL_NAME
import af.amir.mytasky.frameworks.util.ReminderConstant.NOTIFICATION_ID_EXTRA_KEY
import af.amir.mytasky.frameworks.util.ReminderConstant.REMINDER_ACTION_SKIP
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlin.random.Random

object TaskyNotificationManager {

    private val notificationId = Random.nextInt()

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val soundUri = Uri.parse("android.resource://${context.packageName}/raw/notif_wav")
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            channel.setSound(soundUri,audioAttributes)
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        context: Context,
        title: String,
        contentText: String?,
    ) {

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val content = if (contentText.isNullOrBlank()) {
            context.getString(R.string.check_your_today_habits_and_tasks)
        } else {
            contentText + "\n" + context.getString(R.string.check_your_today_habits_and_tasks)
        }

        val pendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val intent = Intent(context, MainActivity::class.java)
        val contentClickPendingIntent = PendingIntent.getActivity(
            context,
            1001,
            intent,
            pendingIntentFlag
        )


        val actionSkipIntent = Intent(context, NotificationDismissReceiver::class.java).apply {
            action = REMINDER_ACTION_SKIP
            putExtra(NOTIFICATION_ID_EXTRA_KEY, notificationId)
        }
        val skipPendingIndent = PendingIntent.getBroadcast(
            context,
            1002,
            actionSkipIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val soundUri = Uri.parse("android.resource://${context.packageName}/raw/notif_wav")

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentClickPendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_close,context.getString(R.string.skip),skipPendingIndent)

        if (Build.VERSION.SDK_INT< Build.VERSION_CODES.O){
            notification.setSound(soundUri)
        }

        manager.notify(notificationId, notification.build())
    }

    fun showDailyCheckReminderNotification(context: Context){
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntentFlag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1002,
            intent,
            pendingIntentFlag
        )


        val soundUri = Uri.parse("android.resource://${context.packageName}/raw/notif_wav")

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.app_name))
            .setSmallIcon(R.mipmap.ic_launcher_adaptive_fore)
            .setContentText(context.getString(R.string.come_and_check_daily_activities))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT< Build.VERSION_CODES.O){
            notification.setSound(soundUri)
        }

        manager.notify(notificationId, notification.build())
    }
}