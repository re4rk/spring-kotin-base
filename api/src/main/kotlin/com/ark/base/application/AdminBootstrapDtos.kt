package com.ark.base.application

data class CreateInitialAdminRequest(
    val email: String,
    val password: String,
    val name: String = "관리자",
)

data class AdminSetPasswordRequest(
    val password: String,
    val currentPassword: String? = null,
)
