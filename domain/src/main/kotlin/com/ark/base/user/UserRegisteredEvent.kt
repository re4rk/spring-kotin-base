package com.ark.base.user

data class UserRegisteredEvent(
    val userId: Long,
    val email: String,
    val name: String,
)
