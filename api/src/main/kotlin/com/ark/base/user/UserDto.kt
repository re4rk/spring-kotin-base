package com.ark.base.user

data class RegisterRequest(
    val email: String,
    val name: String,
    val password: String,
)

data class LoginRequest(
    val email: String,
    val password: String,
)

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
