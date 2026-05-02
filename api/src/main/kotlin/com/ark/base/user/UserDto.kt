package com.ark.base.user

data class RegisterRequest(
    val email: String,
    val name: String,
    val password: String,
) {
    fun toUserRegisterCommand() = UserRegisterCommand(email = email, name = name, rawPassword = password)
}

data class LoginRequest(
    val email: String,
    val password: String,
) {
    fun toUserLoginCommand() = UserLoginCommand(email = email, rawPassword = password)
}

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
) {
    companion object {
        fun from(user: User) = UserResponse(id = user.id, email = user.email, name = user.name)
    }
}

data class TokenResponse(
    val accessToken: String,
    val user: UserResponse,
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
)
