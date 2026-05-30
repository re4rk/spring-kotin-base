package com.ark.base.auth.password.reset

data class PasswordResetRequestedEvent(
    val email: String,
    val resetToken: String,
)
