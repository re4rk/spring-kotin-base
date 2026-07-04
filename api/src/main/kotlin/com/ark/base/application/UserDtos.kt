package com.ark.base.application

import com.ark.base.user.PasswordEncoder
import com.ark.base.user.User
import com.ark.base.user.UserRole

data class RegisterRequest(
    val email: String,
    val name: String,
    val password: String,
) {
    fun toUser(passwordEncoder: PasswordEncoder) =
        User(
            email = email,
            name = name,
            password = password,
            passwordEncoder = passwordEncoder,
        )
}

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
)

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val roles: List<UserRole>,
) {
    companion object {
        fun from(user: User) = UserResponse(id = user.id, email = user.email, name = user.name, roles = listOf(user.role))
    }
}
