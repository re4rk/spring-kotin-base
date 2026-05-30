package com.ark.base.auth.refreshToken

interface RefreshTokenRepository {
    fun issue(userId: Long): RefreshToken

    fun consume(token: String): RefreshTokenConsumeResult

    fun revokeAll(userId: Long)
}
