package af.amir.mytasky

import af.amir.mytasky.frameworks.managers.ReminderManager
import af.amir.mytasky.frameworks.managers.TaskyNotificationManager
import af.amir.mytasky.presentation.timer.TimerManager
import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TaskyApplication : Application() {

    @Inject
    lateinit var reminderManager: ReminderManager

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        TaskyNotificationManager.createNotificationChannel(this)
        TimerManager.createNotificationChannel(this)
        reminderManager.askAlarmPermission()
    }
}