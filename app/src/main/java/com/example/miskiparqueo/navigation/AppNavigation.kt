package com.example.miskiparqueo.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.miskiparqueo.feature.auth.login.presentation.LoginScreen
import com.example.miskiparqueo.feature.auth.signup.presentation.SignUpScreen
import com.example.miskiparqueo.feature.map.presentation.MapScreen

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
                    onNavigateToMap = {
                        navController.navigate(Screen.MapScreen.route) {
                            // Limpiamos el stack para que el usuario no pueda "volver" al login
                            popUpTo(Screen.LoginScreen.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }

        // Pantalla de SignUp
        composable(Screen.SignupScreen.route){
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                SignUpScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateToLogin = {
                        navController.navigate(Screen.LoginScreen.route)
                    },
                    onNavigateToMap = {
                        navController.navigate(Screen.MapScreen.route) {
                            // Limpiamos el stack (hasta el login, que es la raíz de auth)
                            popUpTo(Screen.LoginScreen.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }

        composable(Screen.MapScreen.route) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MapScreen(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}