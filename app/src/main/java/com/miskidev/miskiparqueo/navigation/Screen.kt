package com.miskidev.miskiparqueo.navigation

sealed class Screen(val route: String) {
    object SignupScreen: Screen("signup")
    object LoginScreen: Screen("login")
    object MapScreen: Screen("map")
    object ProfileScreen: Screen("profile")
    object ChangePasswordScreen: Screen("change_password")
    object ReservationScreen: Screen("reservation")
    object ReservationConfirmScreen: Screen("reservation_confirm")
    object ReservationListScreen: Screen("reservation_list")
}
