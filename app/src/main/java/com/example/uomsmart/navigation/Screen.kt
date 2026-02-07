package com.example.uomsmart.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object Access : Screen("access")
    object Canteen : Screen("canteen")
    object About : Screen("about")
    object BookingConfirmed : Screen("booking_confirmed/{token}/{balance}") {
        fun createRoute(token: String, balance: Double) = "booking_confirmed/$token/$balance"
    }
}
