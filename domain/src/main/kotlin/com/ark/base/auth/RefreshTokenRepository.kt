package com.ark.base.auth

interface RefreshTokenRepository {
    fun issue(userId: Long): RefreshToken

    fun consume(token: String): RefreshTokenConsumeResult

    fun revokeAll(userId: Long)
}
