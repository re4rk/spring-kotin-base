package com.ark.base.auth

import com.ark.base.user.UserResponse

data class LoginRequest(
    val email: String,
    val password: String,
) {
    fun toUserLoginCommand() = UserLoginCommand(email = email, rawPassword = password)
}

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
