package com.ark.base.auth.refreshToken

sealed interface RefreshTokenConsumeResult {
    data class Success(
        val userId: Long,
    ) : RefreshTokenConsumeResult

    data class Reused(
        val userId: Long,
    ) : RefreshTokenConsumeResult

    data object Invalid : RefreshTokenConsumeResult

    data object Expired : RefreshTokenConsumeResult
}
