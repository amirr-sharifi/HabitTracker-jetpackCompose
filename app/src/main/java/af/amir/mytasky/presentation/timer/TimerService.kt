package af.amir.mytasky.presentation.timer


import af.amir.mytasky.R
import af.amir.mytasky.domain.model.DailyHabitTimerStatus
import af.amir.mytasky.domain.model.DailyHabitType
import af.amir.mytasky.domain.repository.DailyHabitRepository
import af.amir.mytasky.presentation.timer.TimerConstant.EXTRA_HABIT_ID
import af.amir.mytasky.presentation.util.formatMillisToUserReadable
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var timerJob: Job? = null

    private var activeHabitId: Long = -1L

    @Inject
    lateinit var repository: DailyHabitRepository

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val habitId = intent?.getLongExtra(EXTRA_HABIT_ID, -1L) ?: -1L
        if (habitId == -1L) {
            stopSelf()
            return START_NOT_STICKY
        }
        activeHabitId = habitId

        when (intent?.action) {
            TimerConstant.ACTION_START_RESUME -> handleStartResume()
            TimerConstant.ACTION_PAUSE -> handlePause()
            TimerConstant.ACTION_STOP -> handleStop()
        }

        return START_STICKY
    }

    private fun handleStartResume() {
        serviceScope.launch {
            val habit = repository.getDailyHabit(activeHabitId) ?: return@launch
            val type = habit.type as? DailyHabitType.Timed ?: return@launch

            val newStartTime = System.currentTimeMillis()
            repository.updateHabitTimer(
                habitId = activeHabitId,
                status = DailyHabitTimerStatus.Running,
                startTime = newStartTime,
                accumulatedTime = type.timerAccumulatedTimeMillis
            )

            val initialProgress =
                ((type.timerAccumulatedTimeMillis.toFloat() / type.goal.toFloat()) * 100).toInt()
            val initialTime = formatMillisToUserReadable(type.timerAccumulatedTimeMillis)
            val initialState = LiveTimerData(
                activeHabitId,
                initialTime,
                DailyHabitTimerStatus.Running,
                initialProgress
            )
            startForeground(
                TimerConstant.NOTIFICATION_ID,
                buildNotification(initialState, habit.title)
            )

            runTimerLoop(newStartTime, type.timerAccumulatedTimeMillis, type.goal, habit.title)
        }
    }

    private fun handlePause() {
        timerJob?.cancel()
        serviceScope.launch {
            val habit = repository.getDailyHabit(activeHabitId) ?: return@launch
            val type = habit.type as? DailyHabitType.Timed ?: return@launch
            if (type.timerStatus != DailyHabitTimerStatus.Running) return@launch

            val sessionDuration = System.currentTimeMillis() - habit.timerStartTimeMillis
            val newAccumulatedTime = type.timerAccumulatedTimeMillis + sessionDuration
            repository.updateHabitTimer(
                habitId = activeHabitId,
                status = DailyHabitTimerStatus.Paused,
                startTime = 0L,
                accumulatedTime = newAccumulatedTime
            )

            val progress = ((newAccumulatedTime.toFloat() / type.goal.toFloat()) * 100).toInt()
            val time = formatMillisToUserReadable(newAccumulatedTime)
            val pausedState =
                LiveTimerData(activeHabitId, time, DailyHabitTimerStatus.Paused, progress)
            TimerManager.updateData(pausedState)
            updateNotification(pausedState, habit.title)
        }
    }

    private fun handleStop() {
        timerJob?.cancel()
        serviceScope.launch {
            repository.forceStopTimer(activeHabitId)
            val currentHabit = repository.getDailyHabit(activeHabitId) ?: return@launch
            val timedHabit = (currentHabit.type) as? DailyHabitType.Timed ?: return@launch
            TimerManager.updateData(
                LiveTimerData(
                    status = timedHabit.timerStatus,
                    displayTime = formatMillisToUserReadable(timedHabit.timerAccumulatedTimeMillis),
                    dailyHabitId = activeHabitId
                )
            )
            TimerManager.setCurrentIdNull()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun runTimerLoop(
        sessionStartTime: Long,
        accumulatedTimeBefore: Long,
        goalInMillis: Long,
        habitTitle: String,
    ) {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isActive) {
                val currentSessionElapsed = System.currentTimeMillis() - sessionStartTime
                val totalElapsed = accumulatedTimeBefore + currentSessionElapsed

                if (totalElapsed >= goalInMillis) {
                    repository.updateHabitTimer(
                        activeHabitId,
                        DailyHabitTimerStatus.Complete,
                        0L,
                        goalInMillis
                    )
                    TimerManager.updateData(LiveTimerData(status = DailyHabitTimerStatus.Complete))
                    handleStop()
                    break
                }

                val displayTime = formatMillisToUserReadable(totalElapsed)
                val progressPercent =
                    ((totalElapsed.toFloat() / goalInMillis.toFloat()) * 100).toInt()
                val currentState = LiveTimerData(
                    activeHabitId,
                    displayTime,
                    DailyHabitTimerStatus.Running,
                    progressPercent
                )

                TimerManager.updateData(currentState)
                updateNotification(currentState, habitTitle)

                delay(1000)
            }
        }
    }

    private fun updateNotification(state: LiveTimerData, habitTitle: String) {
        val notification = buildNotification(state, habitTitle)
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(TimerConstant.NOTIFICATION_ID, notification)
    }

    private fun getStopPendingIntent(): PendingIntent {
        val intent = Intent(this, TimerService::class.java).apply {
            setAction(TimerConstant.ACTION_STOP)
            putExtra(TimerConstant.EXTRA_HABIT_ID, activeHabitId)
        }
        return PendingIntent.getService(
            this,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getPlayPausePendingIntent(): PendingIntent {
        val intent = Intent(this, TimerService::class.java).apply {
            val action = when (TimerManager.liveData.value.status) {
                DailyHabitTimerStatus.Idle -> TimerConstant.ACTION_START_RESUME
                DailyHabitTimerStatus.Paused -> TimerConstant.ACTION_START_RESUME
                DailyHabitTimerStatus.Running -> TimerConstant.ACTION_PAUSE
                DailyHabitTimerStatus.Complete -> TimerConstant.ACTION_STOP
            }
            setAction(action)
            putExtra(TimerConstant.EXTRA_HABIT_ID, activeHabitId)
        }
        return PendingIntent.getService(
            this,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun buildNotification(state: LiveTimerData, habitTitle: String): Notification {
        val notificationLayout = RemoteViews(packageName, R.layout.timer_nitification_layout)
        notificationLayout.apply {
            setTextViewText(R.id.tv_habit_name, habitTitle)
            setTextViewText(R.id.tv_display_text, state.displayTime)
            val progressCircle =
                createBitmapCircle(state.progressPercent, 200, 20f, Color.DKGRAY, Color.LTGRAY)
            setImageViewBitmap(R.id.iv_progress_circle, progressCircle)
            val playBtnText =
                if (state.status == DailyHabitTimerStatus.Running) ContextCompat.getString(this@TimerService,R.string.pause) else ContextCompat.getString(this@TimerService,R.string.run)
            setTextViewText(R.id.btn_play_pause, playBtnText)
            setOnClickPendingIntent(R.id.btn_stop, getStopPendingIntent())
            setOnClickPendingIntent(R.id.btn_play_pause, getPlayPausePendingIntent())
        }

        val notification = NotificationCompat.Builder(this, TimerConstant.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setOnlyAlertOnce(true)
            .build()

        return notification
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}


