package com.ark.base.application

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.user.PasswordEncoder
import com.ark.base.user.User
import com.ark.base.user.UserRepository
import com.ark.base.user.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional(readOnly = true)
    fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    @Transactional
    fun changePassword(
        userId: Long,
        request: ChangePasswordRequest,
    ) {
        val user = userRepository.findByIdOrThrow(userId)
        if (!user.matchesPassword(request.currentPassword, passwordEncoder)) throw BaseException(ErrorCode.USER_LOGIN_FAILED)
        user.changePassword(request.newPassword, passwordEncoder)
    }

    @Transactional
    fun delete(
        userId: Long,
        deletedBy: String,
    ) {
        val user = userRepository.findByIdOrThrow(userId)
        user.delete(deletedBy)
    }
}
