package com.app.voicenoteswear.data.auth.repository

import com.app.voicenoteswear.data.apimodels.request.RefreshTokenRequest
import com.app.voicenoteswear.data.apimodels.request.UserLoginRequest
import com.app.voicenoteswear.data.apimodels.response.EmptyResponse
import com.app.voicenoteswear.data.apimodels.response.RefreshTokenResponse
import com.app.voicenoteswear.data.apimodels.response.UserLoginResponse
import com.app.voicenoteswear.data.auth.endpoint.AuthApi
import com.app.voicenoteswear.data.base.BaseRepository
import com.app.voicenoteswear.data.base.Resource
import com.app.voicenoteswear.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : BaseRepository(), AuthRepository {
    override suspend fun login(userLoginRequest: UserLoginRequest): Flow<Resource<UserLoginResponse>> {
        return flow<Resource<UserLoginResponse>> {
            val result = api.login(userLoginRequest)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error((it as Exception)))
        }
    }

    override suspend fun logout(): Flow<Resource<EmptyResponse>> {
        return flow<Resource<EmptyResponse>> {
            val result = api.logout()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error((it as Exception)))
        }
    }

    override suspend fun refresh(token: RefreshTokenRequest): Flow<Resource<RefreshTokenResponse>> {
        return flow<Resource<RefreshTokenResponse>> {
            val result = api.refresh(token)
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error((it as Exception)))
        }
    }
}