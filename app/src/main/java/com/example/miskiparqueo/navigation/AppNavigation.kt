package com.example.miskiparqueo.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miskiparqueo.feature.auth.login.presentation.LoginScreen
import com.example.miskiparqueo.feature.auth.signup.presentation.SignUpScreen
import com.example.miskiparqueo.feature.maintenance.presentation.MaintenanceOverlay
import com.example.miskiparqueo.feature.maintenance.presentation.MaintenanceViewModel
import com.example.miskiparqueo.feature.map.presentation.MapScreen
import com.example.miskiparqueo.feature.profile.presentation.changepassword.ChangePasswordScreen
import com.example.miskiparqueo.feature.profile.presentation.profile.ProfileScreen
import com.example.miskiparqueo.feature.profile.presentation.profile.ProfileViewModel
import com.example.miskiparqueo.feature.reservation.presentation.ReservationConfirmScreen
import com.example.miskiparqueo.feature.reservation.presentation.ReservationListScreen
import com.example.miskiparqueo.feature.reservation.presentation.ReservationScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(){
    // Obtener el ViewModel de mantenimiento
    val maintenanceViewModel: MaintenanceViewModel = koinViewModel()
    val maintenanceMode by maintenanceViewModel.maintenanceMode.collectAsState()

    val navController = rememberNavController()

    // Si la app está en mantenimiento, mostrar solo la pantalla de mantenimiento
    if (maintenanceMode) {
        MaintenanceOverlay()
    } else {
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
                        },
                        onNavigateToReservations = {
                            navController.navigate("${Screen.ReservationListScreen.route}/$userId")
                        },
                        onNavigateToReservation = { parkingId ->
                            navController.navigate("${Screen.ReservationScreen.route}/$userId/$parkingId")
                        }
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
                        },
                        onLogout = {
                            navController.navigate(Screen.LoginScreen.route) {
                                popUpTo(0) { inclusive = true }
                            }
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

            composable(
                route = "${Screen.ReservationScreen.route}/{userId}/{parkingId}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("parkingId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val parkingId = backStackEntry.arguments?.getString("parkingId") ?: return@composable
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                ReservationScreen(
                    modifier = Modifier.fillMaxSize(),
                    userId = userId,
                    parkingId = parkingId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToConfirm = { uid, pid, dateIso, entryIso, exitIso, total ->
                        val encodedDate = Uri.encode(dateIso)
                        val encodedEntry = Uri.encode(entryIso)
                        val encodedExit = Uri.encode(exitIso)
                        val encodedCost = Uri.encode(total.toString())
                        navController.navigate(
                            "${Screen.ReservationConfirmScreen.route}/$uid/$pid/$encodedDate/$encodedEntry/$encodedExit/$encodedCost"
                        )
                    }
                )
            }

            composable(
                route = "${Screen.ReservationConfirmScreen.route}/{userId}/{parkingId}/{date}/{entry}/{exit}/{total}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.StringType },
                    navArgument("parkingId") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("entry") { type = NavType.StringType },
                    navArgument("exit") { type = NavType.StringType },
                    navArgument("total") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                val parkingId = backStackEntry.arguments?.getString("parkingId") ?: return@composable
                val dateIso = backStackEntry.arguments?.getString("date") ?: return@composable
                val entryIso = backStackEntry.arguments?.getString("entry") ?: return@composable
                val exitIso = backStackEntry.arguments?.getString("exit") ?: return@composable
                val totalCost = backStackEntry.arguments?.getString("total")?.let { Uri.decode(it).toDoubleOrNull() } ?: 0.0

                ReservationConfirmScreen(
                    modifier = Modifier.fillMaxSize(),
                    userId = userId,
                    parkingId = parkingId,
                    dateIso = Uri.decode(dateIso),
                    entryTimeIso = Uri.decode(entryIso),
                    exitTimeIso = Uri.decode(exitIso),
                    totalCost = totalCost,
                    onNavigateBack = { navController.popBackStack() },
                    onReservationConfirmed = {
                        navController.popBackStack()
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = "${Screen.ReservationListScreen.route}/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                ReservationListScreen(
                    modifier = Modifier.fillMaxSize(),
                    userId = userId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
