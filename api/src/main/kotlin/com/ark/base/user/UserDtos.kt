package com.ark.base.user

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
) {
    companion object {
        fun from(user: User) = UserResponse(id = user.id, email = user.email, name = user.name)
    }
}
