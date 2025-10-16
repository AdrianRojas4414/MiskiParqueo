package com.example.miskiparqueo.navigation

sealed class Screen(val route: String) {
    object SignupScreen: Screen("signup")
    object LoginScreen: Screen("login")
    // object MapScreen: Screen("map")
}