package af.amir.mytasky.presentation.main

import af.amir.mytasky.presentation.main.routes.BottomBarScreenRoutes
import af.amir.mytasky.presentation.settings.SettingsScreen
import af.amir.mytasky.presentation.home.HomeScreen
import af.amir.mytasky.presentation.manage_data.ManageDataScreen
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


@Composable
fun MainScreen(
    onNewTaskHabitClick: () -> Unit,
    onEditTaskHabitClick: (id: Long,) -> Unit,
) {


    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navHostController = navController) }) {paddingValue->
        SetupBottomBarNavigation(
            modifier =Modifier.padding(paddingValue),
            navHostController = navController,
            onNewTaskHabitClick = onNewTaskHabitClick,  onEditTaskHabitClick = {id ->
                onEditTaskHabitClick(id)
            }
        )
    }

}

@Composable
fun SetupBottomBarNavigation(
    modifier : Modifier,
    navHostController: NavHostController,
    onNewTaskHabitClick: () -> Unit, onEditTaskHabitClick: (id: Long) -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = BottomBarScreenRoutes.TaskHabitListScreen.route
    ) {
        composable(route = BottomBarScreenRoutes.TaskHabitListScreen.route) {
            HomeScreen(onNewTaskHabitClick = onNewTaskHabitClick)
        }
        composable(route = BottomBarScreenRoutes.ManageDataScreen.route) {
            ManageDataScreen(onFinishScreen = {
                navHostController.popBackStack()
            }, onItemClick = { id ->
                onEditTaskHabitClick(id)
            })
        }
        composable(route = BottomBarScreenRoutes.SettingsScreen.route) {
            SettingsScreen()
        }
    }
}

@Composable
fun BottomBar(navHostController: NavHostController) {
    val screens = listOf(
        BottomBarScreenRoutes.TaskHabitListScreen,
        BottomBarScreenRoutes.ManageDataScreen,
        BottomBarScreenRoutes.SettingsScreen
    )

    val navPopStackEntry by navHostController.currentBackStackEntryAsState()
    val currentDestination = navPopStackEntry?.destination

    NavigationBar() {
        screens.forEach {
            AddItem(
                screen = it,
                currentDestination = currentDestination,
                navHostController = navHostController
            )
        }
    }


}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreenRoutes,
    currentDestination: NavDestination?,
    navHostController: NavHostController,
) {
    val selected = currentDestination?.hierarchy?.any {
        it.route == screen.route
    } == true
    NavigationBarItem(selected = selected, onClick = {
        if (!selected) {
            navHostController.navigate(screen.route) {
                popUpTo(navHostController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    }, icon = {
        Icon(imageVector = screen.icon, contentDescription = "")
    })

}

