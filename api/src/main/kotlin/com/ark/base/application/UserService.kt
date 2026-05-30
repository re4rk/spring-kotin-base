package com.ark.base.application

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.user.PasswordEncoder
import com.ark.base.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional(readOnly = true)
    fun findByEmail(email: String) = userRepository.findByEmail(email)

    @Transactional
    fun changePassword(
        userId: Long,
        request: ChangePasswordRequest,
    ) {
        val user = userRepository.getById(userId)
        if (!user.matchesPassword(request.currentPassword, passwordEncoder)) throw BaseException(ErrorCode.USER_LOGIN_FAILED)
        user.changePassword(request.newPassword, passwordEncoder)
    }

    @Transactional
    fun delete(userId: Long) {
        val user = userRepository.getById(userId)
        user.delete()
    }
}
