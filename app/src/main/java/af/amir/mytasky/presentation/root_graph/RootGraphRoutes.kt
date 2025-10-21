package af.amir.mytasky.presentation.root_graph

const val KEY_ID = "ID"

sealed class RootGraphRoutes(val route: String) {
    data object MainScreen : RootGraphRoutes("MainScreen_root_route")
    data object NewTaskHabitScreen :
        RootGraphRoutes("NewTaskHabitScreen_root_route?id={$KEY_ID}") {
            fun passValue(id: Long):String{
                return "NewTaskHabitScreen_root_route?id=$id"
            }
    }
}