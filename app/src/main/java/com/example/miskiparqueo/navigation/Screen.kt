package com.example.miskiparqueo.navigation

sealed class Screen(val route: String) {
    object SignupScreen: Screen("signup")
    //otras
}