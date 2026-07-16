package com.ark.base.ui.auth

import com.ark.base.common.BaseException
import com.ark.base.common.CurrentUserProvider
import com.ark.base.common.ErrorCode
import com.ark.base.user.UserRepository
import org.springframework.stereotype.Component

@Component
class AccessGuard(
    private val currentUserProvider: CurrentUserProvider,
    private val userRepository: UserRepository,
) {
    fun currentUserId(): Long = currentUserProvider.getUserId()

    fun requireSelf(targetUserId: Long) {
        if (currentUserId() != targetUserId) throw BaseException(ErrorCode.ACCESS_DENIED)
    }

    fun requireSelfByEmail(email: String) {
        val user = userRepository.findByEmail(email) ?: return
        requireSelf(user.id)
    }
}
