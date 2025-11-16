package com.example.miskiparqueo.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miskiparqueo.feature.auth.login.presentation.LoginScreen
import com.example.miskiparqueo.feature.auth.signup.presentation.SignUpScreen
import com.example.miskiparqueo.feature.map.presentation.MapScreen
import com.example.miskiparqueo.feature.profile.presentation.changepassword.ChangePasswordScreen
import com.example.miskiparqueo.feature.profile.presentation.profile.ProfileScreen
import com.example.miskiparqueo.feature.profile.presentation.profile.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route,
    ){
        // Pantalla de Login
        composable(Screen.LoginScreen.route) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                LoginScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateToSignUp = {
                        navController.navigate(Screen.SignupScreen.route)
                    },
                    onNavigateToMap = { userId ->
                        navController.navigate("${Screen.MapScreen.route}/$userId") {
                            popUpTo(Screen.LoginScreen.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Pantalla de SignUp
        composable(Screen.SignupScreen.route) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                SignUpScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateToLogin = {
                        navController.navigate(Screen.LoginScreen.route)
                    },
                    onNavigateToMap = { userId ->
                        navController.navigate("${Screen.MapScreen.route}/$userId") {
                            popUpTo(Screen.LoginScreen.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        //Mapa
        composable(
            route = "${Screen.MapScreen.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MapScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateToProfile = {
                        navController.navigate("${Screen.ProfileScreen.route}/$userId")
                    }//,
                    //onNavigateToReservation = TODO()
                )
            }
        }

        // Profile Screen
        composable(
            route = "${Screen.ProfileScreen.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val profileViewModel: ProfileViewModel = koinViewModel()

            LaunchedEffect(key1 = userId) {
                if (userId.isNotEmpty()) {
                    profileViewModel.loadUserById(userId)
                }
            }

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                ProfileScreen(
                    modifier = Modifier.padding(innerPadding),
                    vm = profileViewModel,
                    onNavigateToChangePassword = {
                        navController.navigate("${Screen.ChangePasswordScreen.route}/$userId")
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Change Password Screen
        composable(
            route = "${Screen.ChangePasswordScreen.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ChangePasswordScreen(
                loggedInUserId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}