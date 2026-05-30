package com.ark.base.common

sealed class CurrentUser {
    data class Identified(
        val userId: Long,
    ) : CurrentUser()

    data object Anonymous : CurrentUser()

    val userIdOrNull: Long?
        get() =
            when (this) {
                is Identified -> userId
                Anonymous -> null
            }

    fun requireUserId(): Long = userIdOrNull ?: throw BaseException(ErrorCode.UNAUTHORIZED)
}
