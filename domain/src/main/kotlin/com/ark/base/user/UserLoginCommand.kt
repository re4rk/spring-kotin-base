package com.ark.base.user

data class UserLoginCommand(
    val email: String,
    val rawPassword: String,
)
