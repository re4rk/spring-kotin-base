package com.ark.base.auth

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.common.JwtProvider
import com.ark.base.user.PasswordEncoder
import com.ark.base.user.UserRepository
import com.ark.base.user.UserResponse
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val jwtProvider: JwtProvider,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional(readOnly = true)
    fun login(request: LoginRequest): TokenResponse {
        val user = userRepository.findByEmail(request.email) ?: throw BaseException(ErrorCode.USER_LOGIN_FAILED)
        if (!user.matchesPassword(request.password, passwordEncoder)) throw BaseException(ErrorCode.USER_LOGIN_FAILED)
        return TokenResponse(accessToken = jwtProvider.generate(user.id), user = UserResponse.from(user))
    }

    @Transactional
    fun requestPasswordReset(request: PasswordResetRequest) {
        val user = userRepository.findByEmail(request.email) ?: return
        val passwordResetToken = PasswordResetToken(token = UUID.randomUUID().toString(), userId = user.id)
        passwordResetTokenRepository.save(passwordResetToken)
        eventPublisher.publishEvent(PasswordResetRequestedEvent(user.email, passwordResetToken.token))
    }

    @Transactional
    fun resetPassword(request: PasswordResetConfirmRequest) {
        val userId =
            passwordResetTokenRepository.findById(request.token).orElse(null)?.userId
                ?: throw BaseException(ErrorCode.USER_RESET_TOKEN_INVALID)
        val user = userRepository.getById(userId)
        user.changePassword(request.newPassword, passwordEncoder)
        passwordResetTokenRepository.deleteById(request.token)
    }
}
