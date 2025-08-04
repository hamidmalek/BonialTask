package com.example.bonialtask.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bonialtask.ui.brochure.BrochureListScreen
import com.example.bonialtask.ui.brochure.BrochureViewModel
import com.example.bonialtask.ui.splash.SplashScreen
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash");
    object Brochures : Screen("brochures")
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen()
            LaunchedEffect(Unit) {
                delay(2000)
                navController.navigate(Screen.Brochures.route) {
                    popUpTo(
                        Screen.Splash.route
                    ) { inclusive = true }
                }
            }
        }
        composable(Screen.Brochures.route) {
            val vm: BrochureViewModel = koinViewModel()
            BrochureListScreen(vm)
        }
    }
}