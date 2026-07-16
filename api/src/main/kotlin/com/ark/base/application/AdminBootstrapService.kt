package com.ark.base.application

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.user.PasswordEncoder
import com.ark.base.user.User
import com.ark.base.user.UserRepository
import com.ark.base.user.UserRole
import com.ark.base.user.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminBootstrapService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional(readOnly = true)
    fun needsSetup(): Boolean = userRepository.count() == 0L

    @Transactional
    fun createInitialAdmin(request: CreateInitialAdminRequest): User {
        if (userRepository.count() > 0L) {
            throw BaseException(ErrorCode.ADMIN_SETUP_ALREADY_DONE)
        }
        validateNewPassword(request.password)
        val email = request.email.trim()
        if (userRepository.findByEmail(email) != null) {
            throw BaseException(ErrorCode.USER_DUPLICATE_EMAIL)
        }
        val user =
            User(
                email = email,
                name = request.name.trim().ifBlank { "관리자" },
                password = request.password,
                passwordEncoder = passwordEncoder,
            ).also { it.role = UserRole.ADMIN }
        return userRepository.save(user)
    }

    @Transactional
    fun setPassword(
        userId: Long,
        request: AdminSetPasswordRequest,
    ) {
        val user = userRepository.findByIdOrThrow(userId)
        validateNewPassword(request.password)
        if (user.hasPassword()) {
            val current =
                request.currentPassword?.takeIf { it.isNotBlank() }
                    ?: throw BaseException(ErrorCode.USER_LOGIN_FAILED)
            if (!user.matchesPassword(current, passwordEncoder)) {
                throw BaseException(ErrorCode.USER_LOGIN_FAILED)
            }
        }
        user.changePassword(request.password, passwordEncoder)
    }

    private fun validateNewPassword(password: String) {
        if (password.length < MIN_PASSWORD_LENGTH) {
            throw BaseException(ErrorCode.ADMIN_PASSWORD_TOO_SHORT)
        }
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
}
