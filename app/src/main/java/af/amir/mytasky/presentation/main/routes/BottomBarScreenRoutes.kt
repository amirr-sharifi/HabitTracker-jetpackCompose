package af.amir.mytasky.presentation.main.routes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material.icons.twotone.DateRange
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreenRoutes(
    val route :  String,
    val icon : ImageVector
) {

    data object TaskHabitListScreen : BottomBarScreenRoutes(
        route = "taskHabitListScreen_route",
        icon = Icons.TwoTone.DateRange
    )

    data object ManageDataScreen : BottomBarScreenRoutes(
        route = "ManageDataScreen_route",
        icon = Icons.TwoTone.CheckCircle
    )

    data object SettingsScreen: BottomBarScreenRoutes(
        route = "SettingsScreen_route",
        icon = Icons.TwoTone.Settings
    )

}