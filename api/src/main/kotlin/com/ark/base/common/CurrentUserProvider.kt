package com.ark.base.common

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class CurrentUserProvider {
    fun getCurrentUser(): CurrentUser = getUserIdOrNull()?.let { CurrentUser.Identified(it) } ?: CurrentUser.Anonymous

    fun getUserId(): Long = getUserIdOrNull() ?: throw BaseException(ErrorCode.UNAUTHORIZED)

    fun getUserIdOrNull(): Long? {
        val principal = SecurityContextHolder.getContext().authentication?.principal
        return when (principal) {
            is SecurityUser -> principal.userId
            else -> null
        }
    }
}
