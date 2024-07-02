package com.app.voicenoteswear.navigation.auth

sealed class AuthNavigation(val route: String) {
    object WelcomeScreen : AuthNavigation("welcome_screen")
}
