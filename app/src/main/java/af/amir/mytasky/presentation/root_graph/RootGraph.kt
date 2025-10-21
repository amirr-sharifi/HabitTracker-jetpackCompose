package af.amir.mytasky.presentation.root_graph

import af.amir.mytasky.presentation.edit_habit.EditHabitViewModel
import af.amir.mytasky.presentation.main.MainScreen
import af.amir.mytasky.presentation.edit_habit.NewEditTaskHabitScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun SetupRootGraph(navHostController: NavHostController,modifier: Modifier = Modifier) {

    NavHost(modifier = modifier,
        navController = navHostController,
        startDestination = RootGraphRoutes.MainScreen.route
    ) {
        composable(route = RootGraphRoutes.MainScreen.route) {
            MainScreen(onNewTaskHabitClick = {
                navHostController.navigate(RootGraphRoutes.NewTaskHabitScreen.route)
            }, onEditTaskHabitClick = { id, ->
                navHostController.navigate(
                    RootGraphRoutes.NewTaskHabitScreen.passValue(
                        id,
                    )
                )
            })
        }
        composable(route = RootGraphRoutes.NewTaskHabitScreen.route, arguments = listOf(
            navArgument(name = KEY_ID) {
                type = NavType.LongType
                defaultValue = -1L
            },
        )) {
            val viewModel: EditHabitViewModel = hiltViewModel()
            NewEditTaskHabitScreen (viewModel = viewModel){
                navHostController.popBackStack()
            }
        }
    }

}