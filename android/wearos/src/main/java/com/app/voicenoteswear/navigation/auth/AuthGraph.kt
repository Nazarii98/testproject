package com.app.voicenoteswear.navigation.auth

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.app.voicenoteswear.navigation.AppNavigation
import com.app.voicenoteswear.presentation.flow.auth.WelcomeScreen

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation(
        startDestination = AuthNavigation.WelcomeScreen.route,
        route = AppNavigation.AuthFlow.route
    ) {
        composable(AuthNavigation.WelcomeScreen.route) {
            Log.d("Navigation", "In AuthGraph: WelcomeScreen")
            WelcomeScreen(
                onLogin = {
                    Log.d("Navigation", "Navigating to MainFlow")
                    navController.navigate(AppNavigation.MainFlow.route) {
                        popUpTo(AppNavigation.AuthFlow.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}