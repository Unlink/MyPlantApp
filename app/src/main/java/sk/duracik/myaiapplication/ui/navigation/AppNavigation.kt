package sk.duracik.myaiapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import sk.duracik.myaiapplication.PlantApplication
import sk.duracik.myaiapplication.ui.screens.AddPlantScreen
import sk.duracik.myaiapplication.ui.screens.HomeScreen
import sk.duracik.myaiapplication.ui.screens.PlantDetailScreen
import sk.duracik.myaiapplication.viewmodel.AddPlantViewModel
import sk.duracik.myaiapplication.viewmodel.HomeViewModel
import sk.duracik.myaiapplication.viewmodel.PlantDetailViewModel

// Definícia navigačných cieľov
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object PlantDetail : Screen("plant/{plantId}") {
        fun createRoute(plantId: Int) = "plant/$plantId"
    }
    object AddPlant : Screen("add_plant")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    // Získanie repozitára z aplikačnej triedy
    val context = LocalContext.current
    val repository = (context.applicationContext as PlantApplication).repository

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            // Vytvorenie HomeViewModel s repozitárom
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.HomeViewModelFactory(repository)
            )

            HomeScreen(
                homeViewModel = homeViewModel,
                onPlantClick = { plantId ->
                    navController.navigate(Screen.PlantDetail.createRoute(plantId))
                },
                onAddPlantClick = {
                    navController.navigate(Screen.AddPlant.route)
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

            // Vytvorenie PlantDetailViewModel s repozitárom
            val plantDetailViewModel: PlantDetailViewModel = viewModel(
                factory = PlantDetailViewModel.PlantDetailViewModelFactory(repository)
            )

            PlantDetailScreen(
                plantId = plantId,
                plantDetailViewModel = plantDetailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddPlant.route) {
            // Vytvorenie AddPlantViewModel s repozitárom
            val addPlantViewModel: AddPlantViewModel = viewModel(
                factory = AddPlantViewModel.AddPlantViewModelFactory(repository)
            )

            AddPlantScreen(
                addPlantViewModel = addPlantViewModel,
                onNavigateBack = { navController.popBackStack() },
                onPlantAdded = {
                    // Po úspešnom pridaní rastliny sa vrátime na domovskú obrazovku
                    navController.popBackStack()
                }
            )
        }
    }
}
