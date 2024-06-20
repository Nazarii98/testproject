package com.app.voicenoteswear.navigation

sealed class AppNavigation(val route: String) {
    object AuthFlow : AppNavigation("auth")
    object MainFlow : AppNavigation("main")
}