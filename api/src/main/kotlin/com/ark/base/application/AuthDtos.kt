package com.ark.base.application

data class LoginRequest(
    val email: String,
    val password: String,
)

data class TokenResponse(
    val accessToken: String,
    val user: UserResponse,
)

data class PasswordResetRequest(
    val email: String,
)

data class PasswordResetConfirmRequest(
    val token: String,
    val newPassword: String,
)
