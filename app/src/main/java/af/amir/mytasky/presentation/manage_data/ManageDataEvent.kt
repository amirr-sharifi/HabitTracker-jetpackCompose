package af.amir.mytasky.presentation.manage_data

import af.amir.mytasky.presentation.manage_data.model.CategoryDisplayItem

sealed interface ManageDataEvent {
    data class OnItemClick(val item : CategoryDisplayItem) : ManageDataEvent
    data object OnBack : ManageDataEvent
}