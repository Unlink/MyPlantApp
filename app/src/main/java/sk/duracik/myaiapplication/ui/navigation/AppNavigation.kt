package sk.duracik.myaiapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import sk.duracik.myaiapplication.ui.screens.HomeScreen
import sk.duracik.myaiapplication.ui.screens.PlantDetailScreen

// Definícia navigačných cieľov
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object PlantDetail : Screen("plant/{plantId}") {
        fun createRoute(plantId: Int) = "plant/$plantId"
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onPlantClick = { plantId ->
                    navController.navigate(Screen.PlantDetail.createRoute(plantId))
                }
            )
        }

        composable(
            route = Screen.PlantDetail.route,
            arguments = listOf(
                navArgument("plantId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getInt("plantId") ?: return@composable
            PlantDetailScreen(
                plantId = plantId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
