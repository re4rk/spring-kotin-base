package com.ark.base.auth

data class PasswordResetRequestedEvent(
    val email: String,
    val resetToken: String,
)
