package com.app.voicenoteswear.domain.auth.repository

import com.app.voicenoteswear.data.apimodels.request.RefreshTokenRequest
import com.app.voicenoteswear.data.apimodels.request.UserLoginRequest
import com.app.voicenoteswear.data.apimodels.response.EmptyResponse
import com.app.voicenoteswear.data.apimodels.response.RefreshTokenResponse
import com.app.voicenoteswear.data.apimodels.response.UserLoginResponse
import com.app.voicenoteswear.data.base.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(userLoginRequest: UserLoginRequest): Flow<Resource<UserLoginResponse>>
    suspend fun logout(): Flow<Resource<EmptyResponse>>
    suspend fun refresh(token: RefreshTokenRequest): Flow<Resource<RefreshTokenResponse>>
}