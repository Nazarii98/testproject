package com.app.voicenoteswear.navigation.main

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.app.voicenoteswear.navigation.AppNavigation
import com.app.voicenoteswear.presentation.flow.main.home.HomeScreen
import com.app.voicenoteswear.presentation.flow.main.note_detail.NoteDetailScreen
import com.app.voicenoteswear.presentation.flow.shared.NoteSharedViewModel

fun NavGraphBuilder.mainGraph(navController: NavController, noteSharedViewModel: NoteSharedViewModel) {
    navigation(
        startDestination = MainNavigation.HomeScreen.route,
        route = AppNavigation.MainFlow.route
    ) {
        composable(MainNavigation.HomeScreen.route) { entry ->
            val refresh = entry.savedStateHandle.get<Boolean>("refresh") ?: false
            Log.d("Navigation", "In MainGraph: HomeScreen")
            HomeScreen(
                sharedViewModel = noteSharedViewModel,
                refresh = refresh,
                navigateToNoteDetail = {
                    navController.navigate(MainNavigation.NoteDetailScreen.route)
                },
                onLogout = {
                    Log.d("Navigation", "Navigating to AuthFlow")
                    navController.navigate(AppNavigation.AuthFlow.route) {
                        popUpTo(AppNavigation.MainFlow.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(MainNavigation.NoteDetailScreen.route) {
            Log.d("Navigation", "In MainGraph: NoteDetailScreen")
            NoteDetailScreen(
                sharedViewModel = noteSharedViewModel,
                onBack = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("refresh", it)
                    navController.popBackStack()
                }
            )
        }
    }
}