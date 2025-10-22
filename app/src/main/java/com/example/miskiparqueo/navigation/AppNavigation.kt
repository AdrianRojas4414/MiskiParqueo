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
import com.example.miskiparqueo.feature.profile.presentation.changepassword.ChangePasswordScreen
import com.example.miskiparqueo.feature.profile.presentation.profile.ProfileScreen
import com.example.miskiparqueo.feature.profile.presentation.profile.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route,
    ) {
        // Pantalla de Login
        composable(Screen.LoginScreen.route) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                LoginScreen(
                    modifier = Modifier.padding(innerPadding),
                    onNavigateToSignUp = {
                        navController.navigate(Screen.SignupScreen.route)
                    },
                    // Define la acción de navegar al perfil cuando el login es exitoso
                    onNavigateToProfile = { userId ->
                        navController.navigate("${Screen.ProfileScreen.route}/$userId") {
                            // Limpia el historial para que el usuario no pueda volver al login
                            popUpTo(Screen.LoginScreen.route) {
                                inclusive = true
                            }
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
                    // Define la acción de navegar al login cuando el registro es exitoso
                    onNavigateToLogin = {
                        navController.navigate(Screen.LoginScreen.route) {
                            // Limpia el historial para que el usuario no vuelva al registro
                            popUpTo(Screen.SignupScreen.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }

        // Pantalla de Perfil
        composable(
            route = "${Screen.ProfileScreen.route}/{userId}", // Ruta que espera un argumento
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Extrae el userId de la ruta
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            // Obtiene una instancia del ViewModel para el perfil
            val profileViewModel: ProfileViewModel = koinViewModel()

            // Este efecto se ejecuta una vez para cargar los datos del usuario
            LaunchedEffect(key1 = userId) {
                if (userId.isNotEmpty()) {
                    profileViewModel.loadUserById(userId)
                }
            }

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                ProfileScreen(
                    modifier = Modifier.padding(innerPadding),
                    vm = profileViewModel, // Pasa el ViewModel que ya contiene los datos del usuario
                    onNavigateToChangePassword = {
                        // Navega a la pantalla de cambiar contraseña, pasando el mismo userId
                        navController.navigate("${Screen.ChangePasswordScreen.route}/$userId")
                    }
                )
            }
        }

        // Pantalla de Cambiar Contraseña
        composable(
            route = "${Screen.ChangePasswordScreen.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ChangePasswordScreen(
                loggedInUserId = userId, // Recuerda que debes pasar este userId a tu ChangePasswordScreen para que funcione
                onNavigateBack = { navController.popBackStack() } // Acción para volver atrás
            )
        }
    }
}