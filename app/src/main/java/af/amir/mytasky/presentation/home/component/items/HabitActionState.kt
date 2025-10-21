package af.amir.mytasky.presentation.home.component.items

import af.amir.mytasky.domain.model.DailyHabitTimerStatus
import af.amir.mytasky.presentation.model.CountableHabitUiModel
import af.amir.mytasky.presentation.model.DailyHabitUiModel
import af.amir.mytasky.presentation.model.SimpleHabitUiModel
import af.amir.mytasky.presentation.model.TimedHabitUiModel

sealed class HabitActionState{
    object Idle  : HabitActionState()
    object InProgress  : HabitActionState()
    object Complete  : HabitActionState()
}

fun DailyHabitUiModel.toHabitActionState(): HabitActionState {
    return when(this){
        is CountableHabitUiModel -> if (goal == current) HabitActionState.Complete else HabitActionState.Idle
        is SimpleHabitUiModel -> if (isDone) HabitActionState.Complete else HabitActionState.Idle
        is TimedHabitUiModel -> {
            when(timerStatus){
                DailyHabitTimerStatus.Idle -> HabitActionState.Idle
                DailyHabitTimerStatus.Paused -> HabitActionState.Idle
                DailyHabitTimerStatus.Running -> HabitActionState.InProgress
                DailyHabitTimerStatus.Complete -> HabitActionState.Complete
            }
        }
    }
}
