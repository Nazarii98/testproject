package com.app.voicenoteswear.navigation.main

sealed class MainNavigation(val route: String) {
    object HomeScreen : MainNavigation("home_screen")
    object NoteDetailScreen : MainNavigation("note_detail_screen")
    object RecordScreen : MainNavigation("record_screen")
}
