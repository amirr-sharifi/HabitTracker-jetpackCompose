package af.amir.mytasky.presentation.timer

import af.amir.mytasky.R
import android.content.Context
import android.content.Intent
import af.amir.mytasky.domain.model.DailyHabitTimerStatus
import af.amir.mytasky.domain.repository.DailyHabitRepository
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LiveTimerData(
    val dailyHabitId: Long? = null,
    val displayTime: String = "00:00",
    val status: DailyHabitTimerStatus = DailyHabitTimerStatus.Idle,
    val progressPercent: Int = 0,
)

object TimerManager {
    private val _liveData = MutableStateFlow(LiveTimerData())
    val liveData = _liveData.asStateFlow()

    var currentlyActiveHabitId: Long? = null
        private set
    private val scope = CoroutineScope(Dispatchers.IO)

    fun updateData(data: LiveTimerData) {
        _liveData.value = data
    }

    fun startOrResume(context: Context, habitId: Long) {
        scope.launch {
            if (currentlyActiveHabitId != null && currentlyActiveHabitId != habitId) {
                withContext(Main) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.timer_already_exist),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }

            currentlyActiveHabitId = habitId

            val intent = Intent(context, TimerService::class.java).apply {
                action = TimerConstant.ACTION_START_RESUME
                putExtra(TimerConstant.EXTRA_HABIT_ID, habitId)
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }

    fun pause(context: Context) {
        if (currentlyActiveHabitId == null) return

        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerConstant.ACTION_PAUSE
            putExtra(TimerConstant.EXTRA_HABIT_ID, currentlyActiveHabitId)
        }
        context.startService(intent)
        setCurrentIdNull()
    }

    fun setCurrentIdNull() {
        currentlyActiveHabitId = null
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                TimerConstant.CHANNEL_ID,
                TimerConstant.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

}

