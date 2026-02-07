package com.example.uomsmart.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.uomsmart.screens.about.AboutScreen
import com.example.uomsmart.screens.access.AccessScreen
import com.example.uomsmart.screens.canteen.BookingConfirmedScreen
import com.example.uomsmart.screens.canteen.CanteenScreen
import com.example.uomsmart.screens.home.HomeScreen
import com.example.uomsmart.screens.login.LoginScreen
import com.example.uomsmart.screens.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Show bottom nav only on main screens
    val showBottomNav = currentRoute in listOf(
        Screen.Home.route,
        Screen.Canteen.route,
        Screen.Access.route,
        Screen.About.route
    )
    
    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = Screen.Splash.route) {
                SplashScreen(
                    onSplashComplete = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(route = Screen.Login.route) {
                LoginScreen(
                    onLoginClick = { email, password ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onSsoClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onForgotPasswordClick = {
                        // TODO: Handle forgot password
                    }
                )
            }
            
            composable(route = Screen.Home.route) {
                HomeScreen(
                    onAiScoutClick = {
                        // TODO: Open AI Scout dialog
                    }
                )
            }
            
            composable(route = Screen.Canteen.route) {
                CanteenScreen(
                    onBackClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onMealBooked = { meal, newBalance ->
                        val token = "UOM-M#${(100000..999999).random().toString(16).uppercase()}"
                        navController.navigate(
                            Screen.BookingConfirmed.createRoute(token, newBalance)
                        )
                    }
                )
            }
            
            composable(route = Screen.Access.route) {
                AccessScreen(
                    onBackClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(route = Screen.About.route) {
                AboutScreen(
                    onBackClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(
                route = Screen.BookingConfirmed.route,
                arguments = listOf(
                    navArgument("token") { type = NavType.StringType },
                    navArgument("balance") { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val token = backStackEntry.arguments?.getString("token") ?: ""
                val balance = backStackEntry.arguments?.getFloat("balance")?.toDouble() ?: 0.0
                BookingConfirmedScreen(
                    mealToken = token,
                    updatedBalance = balance,
                    onGoToDashboard = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
