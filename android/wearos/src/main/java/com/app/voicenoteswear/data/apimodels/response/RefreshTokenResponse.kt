package com.app.voicenoteswear.data.apimodels.response

data class RefreshTokenResponse(
    val authorisation: Authorisation? = null
)

data class Authorisation(
    val token: String
)
