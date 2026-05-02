package com.ark.base.user

data class UserRegisterCommand(
    val email: String,
    val name: String,
    val rawPassword: String,
) {
    fun toUser(passwordEncoder: PasswordEncoder) = User(email = email, name = name, passwordHash = passwordEncoder.encode(rawPassword))
}
