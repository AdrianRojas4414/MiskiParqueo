package com.example.miskiparqueo.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.miskiparqueo.feature.auth.signup.presentation.SignUpScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.SignupScreen.route,
        /*enterTransition = { EnterTransition.None},
        exitTransition = { ExitTransition.None},*/
    ){
        composable(Screen.SignupScreen.route){
            Scaffold(modifier = Modifier.fillMaxSize()) {
                    innerPadding ->
                SignUpScreen(modifier = Modifier.padding(innerPadding),
                    //onNavigateToLogin = TODO(), onNavigateToMap = TODO()
                )
            }
        }
    }
}