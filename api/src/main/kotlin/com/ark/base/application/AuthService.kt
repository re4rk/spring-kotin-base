package com.ark.base.application

import com.ark.base.auth.PasswordResetRequestedEvent
import com.ark.base.auth.PasswordResetToken
import com.ark.base.auth.PasswordResetTokenRepository
import com.ark.base.auth.RefreshTokenConsumeResult
import com.ark.base.auth.RefreshTokenRepository
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.common.JwtProvider
import com.ark.base.user.PasswordEncoder
import com.ark.base.user.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProvider: JwtProvider,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun login(request: LoginRequest): TokenResponse {
        val user = userRepository.findByEmail(request.email) ?: throw BaseException(ErrorCode.USER_LOGIN_FAILED)
        if (!user.matchesPassword(request.password, passwordEncoder)) throw BaseException(ErrorCode.USER_LOGIN_FAILED)
        val refreshToken = refreshTokenRepository.issue(user.id)
        return TokenResponse(
            accessToken = jwtProvider.generate(user.id),
            refreshToken = refreshToken.token,
        )
    }

    @Transactional
    fun refresh(request: RefreshTokenRequest): TokenResponse {
        when (val result = refreshTokenRepository.consume(request.refreshToken)) {
            is RefreshTokenConsumeResult.Success -> {
                val user = userRepository.getById(result.userId)
                val refreshToken = refreshTokenRepository.issue(user.id)
                return TokenResponse(
                    accessToken = jwtProvider.generate(user.id),
                    refreshToken = refreshToken.token,
                )
            }
            is RefreshTokenConsumeResult.Reused -> {
                refreshTokenRepository.revokeAll(result.userId)
                throw BaseException(ErrorCode.USER_REFRESH_TOKEN_REUSED)
            }
            RefreshTokenConsumeResult.Invalid -> throw BaseException(ErrorCode.USER_REFRESH_TOKEN_INVALID)
        }
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

    @Transactional
    fun register(request: RegisterRequest): UserResponse {
        if (userRepository.findByEmail(request.email) != null) throw BaseException(ErrorCode.USER_DUPLICATE_EMAIL)
        val user = userRepository.save(request.toUser(passwordEncoder))
        return UserResponse.from(user)
    }
}
