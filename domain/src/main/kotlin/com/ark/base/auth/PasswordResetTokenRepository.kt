package com.ark.base.auth

interface PasswordResetTokenRepository {
    fun save(
        token: String,
        userId: Long,
    )

    fun getUserId(token: String): Long?

    fun delete(token: String)
}
