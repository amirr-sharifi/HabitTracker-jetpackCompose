package af.amir.mytasky

import af.amir.mytasky.presentation.root_graph.SetupRootGraph
import af.amir.mytasky.presentation.theme.MyTaskyTheme
import af.amir.mytasky.util.DarkTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.annotation.SuppressLint
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()


    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val darkTheme = when (uiState.darkTheme) {
                DarkTheme.On -> true
                DarkTheme.Off -> false
                DarkTheme.System -> isSystemInDarkTheme()
                DarkTheme.Default -> isSystemInDarkTheme()
            }
            val navController = rememberNavController()
            splash.setKeepOnScreenCondition {
                uiState.isLoading
            }

            MyTaskyTheme(
                dynamicColor = uiState.dynamicColor,
                darkTheme = darkTheme,
                themeColor = uiState.themeColor
            ) {
                SetupRootGraph(
                    navHostController = navController,
                )


            }
        }
    }


}

