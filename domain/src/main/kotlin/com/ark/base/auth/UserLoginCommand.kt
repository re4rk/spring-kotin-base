package com.ark.base.auth

data class UserLoginCommand(
    val email: String,
    val rawPassword: String,
)
