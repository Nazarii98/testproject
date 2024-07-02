package com.app.voicenoteswear.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.app.voicenoteswear.data.datastorage.UserDataStorage
import com.app.voicenoteswear.domain.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userDataStorage: UserDataStorage,
    private val authRepository: AuthRepository
) : ViewModel() {
    var startDestination: DirectionState? by mutableStateOf(DirectionState.Auth)
        private set

    init {
        val currentUserAccessToken = userDataStorage.accessToken
        startDestination = if (currentUserAccessToken.isNotEmpty()) {
            DirectionState.Main
        } else {
            DirectionState.Auth
        }
    }
}

enum class DirectionState {
    Auth, Main
}