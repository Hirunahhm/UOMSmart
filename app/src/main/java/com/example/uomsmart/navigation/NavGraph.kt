package com.example.uomsmart.navigation

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.uomsmart.R
import com.example.uomsmart.screens.about.AboutScreen
import com.example.uomsmart.screens.access.AccessScreen
import com.example.uomsmart.screens.canteen.BookingConfirmedScreen
import com.example.uomsmart.screens.canteen.CanteenScreen
import com.example.uomsmart.screens.home.HomeScreen
import com.example.uomsmart.screens.login.LoginScreen
import com.example.uomsmart.screens.splash.SplashScreen
import com.example.uomsmart.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun NavGraph(navController: NavHostController, startDestination: String = Screen.Splash.route) {
    val authViewModel: AuthViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Show bottom nav only on main screens
    val showBottomNav =
            currentRoute in
                    listOf(
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
                            // Check if user is already logged in
                            if (authViewModel.isLoggedIn) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        }
                )
            }

            composable(route = Screen.Login.route) {
                val context = LocalContext.current

                // Google Sign-In launcher
                val googleSignInLauncher =
                        rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartActivityForResult()
                        ) { result ->
                            if (result.resultCode == Activity.RESULT_OK) {
                                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                                try {
                                    val account = task.getResult(ApiException::class.java)
                                    account?.idToken?.let { token ->
                                        authViewModel.signInWithGoogle(token) {
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo(Screen.Login.route) { inclusive = true }
                                            }
                                        }
                                    }
                                } catch (e: ApiException) {
                                    Log.e("GoogleSignIn", "Sign in failed", e)
                                }
                            }
                        }

                LoginScreen(
                        onSignIn = { email, password ->
                            authViewModel.signIn(email, password) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        },
                        onSignUp = { name, email, password ->
                            authViewModel.signUp(name, email, password) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        },
                        onGoogleSignIn = {
                            val gso =
                                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .requestIdToken(
                                                    context.getString(
                                                            R.string.default_web_client_id
                                                    )
                                            )
                                            .requestEmail()
                                            .build()
                            val googleSignInClient = GoogleSignIn.getClient(context, gso)
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        },
                        onForgotPasswordClick = {
                            // TODO: Navigate to forgot password screen
                        },
                        isLoading = authViewModel.isLoading,
                        errorMessage = authViewModel.errorMessage
                )
            }

            composable(route = Screen.Home.route) {
                HomeScreen(
                        onAiScoutClick = {
                            navController.navigate(Screen.SustainabilityScout.route)
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
                        onTransactionHistoryClick = {
                            navController.navigate(Screen.TransactionHistory.route)
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
                        },
                        onLogoutClick = {
                            authViewModel.signOut()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                )
            }

            composable(
                    route = Screen.BookingConfirmed.route,
                    arguments =
                            listOf(
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

            composable(route = Screen.TransactionHistory.route) {
                com.example.uomsmart.screens.wallet.TransactionHistoryScreen(
                        onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.SustainabilityScout.route) {
                com.example.uomsmart.screens.scout.ScoutScreen(
                        onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
