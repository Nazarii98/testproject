package com.app.voicenoteswear.data.apimodels.response

data class UserLoginResponse(
    val status: String,
    val user: User? = null,
    val authorisation: Authorization? = null,
    val errors: Errors,
    val message: String
)

data class User(
    val id: Int,
    val name: String,
    val email: String,

)

data class Authorization(
    val token: String,
    val type: String
)

data class Errors(
    val password: List<String>?
)