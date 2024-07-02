package com.app.voicenoteswear.data.auth.endpoint

import com.app.voicenoteswear.data.apimodels.request.RefreshTokenRequest
import com.app.voicenoteswear.data.apimodels.request.UserLoginRequest
import com.app.voicenoteswear.data.apimodels.response.EmptyResponse
import com.app.voicenoteswear.data.apimodels.response.RefreshTokenResponse
import com.app.voicenoteswear.data.apimodels.response.UserLoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body requestBody: UserLoginRequest): UserLoginResponse

    @POST("api/auth/logout")
    suspend fun logout(): EmptyResponse

    @POST("api/auth/refresh")
    suspend fun refresh(@Body token: RefreshTokenRequest): RefreshTokenResponse

    @GET("api/auth/me")
    suspend fun getUserData(): EmptyResponse
}