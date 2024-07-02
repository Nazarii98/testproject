package com.app.voicenoteswear.presentation.flow.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.voicenoteswear.data.base.Resource
import com.app.voicenoteswear.data.datastorage.UserDataStorage
import com.app.voicenoteswear.domain.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userDataStorage: UserDataStorage
) : ViewModel() {

    private val _state = MutableSharedFlow<State>()
    val state: SharedFlow<State> = _state

    sealed interface State {
        object Loading : State
        object LoginSuccess : State
        class AuthError(val message: String) : State
    }

    fun login() {
        viewModelScope.launch {
            if (userDataStorage.accessToken.isNotEmpty()) {
                _state.emit(State.LoginSuccess)
            }
//            val userLoginRequest = UserLoginRequest("kovedik@gmail.com", "String123!")
//            val result = authRepository.login(userLoginRequest)
//            result.collectLatest {
//                when(it) {
//                    is Resource.Error -> {
//
//                    }
//                    Resource.Loading -> {
//                        _state.emit(State.Loading)
//                    }
//                    is Resource.Success -> {
//                        val token = it.data.authorisation?.token
//                        token?.let {
//                            userDataStorage.accessToken = it
//                        }
//                        _state.emit(State.LoginSuccess)
//                    }
//                }
//            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val result = authRepository.logout()
            result.collectLatest {
                when(it) {
                    is Resource.Error -> {

                    }
                    Resource.Loading -> {
                        _state.emit(State.Loading)
                    }
                    is Resource.Success -> {
                        userDataStorage.accessToken = ""
                    }
                }
            }
        }
    }
}