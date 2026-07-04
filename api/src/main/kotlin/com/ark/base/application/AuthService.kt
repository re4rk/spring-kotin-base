package com.ark.base.application

import com.ark.base.auth.password.reset.PasswordResetRequestedEvent
import com.ark.base.auth.password.reset.PasswordResetToken
import com.ark.base.auth.password.reset.PasswordResetTokenRepository
import com.ark.base.auth.password.reset.findByIdOrThrow
import com.ark.base.auth.refreshToken.RefreshTokenConsumeResult
import com.ark.base.auth.refreshToken.RefreshTokenRepository
import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.common.JwtProvider
import com.ark.base.common.PasswordTransportResolver
import com.ark.base.user.PasswordEncoder
import com.ark.base.user.UserRepository
import com.ark.base.user.UserRole
import com.ark.base.user.findByIdOrThrow
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
    private val passwordTransportResolver: PasswordTransportResolver,
) {
    @Transactional
    fun login(request: LoginRequest): TokenResponse {
        val user = userRepository.findByEmail(request.email) ?: throw BaseException(ErrorCode.USER_LOGIN_FAILED)

        if (!user.matchesPassword(passwordTransportResolver.resolve(request.password), passwordEncoder)) {
            throw BaseException(ErrorCode.USER_LOGIN_FAILED)
        }

        val accessToken = jwtProvider.generate(user.id)
        val refreshToken = refreshTokenRepository.issue(user.id)
        return TokenResponse(accessToken = accessToken, refreshToken = refreshToken.token)
    }

    @Transactional
    fun refresh(request: RefreshTokenRequest): TokenResponse {
        when (val result = refreshTokenRepository.consume(request.refreshToken)) {
            is RefreshTokenConsumeResult.Success -> {
                val user = userRepository.findByIdOrThrow(result.userId)
                val accessToken = jwtProvider.generate(user.id)
                val refreshToken = refreshTokenRepository.issue(user.id)
                return TokenResponse(accessToken = accessToken, refreshToken = refreshToken.token)
            }
            is RefreshTokenConsumeResult.Reused -> {
                refreshTokenRepository.revokeAll(result.userId)
                throw BaseException(ErrorCode.USER_REFRESH_TOKEN_REUSED)
            }
            RefreshTokenConsumeResult.Invalid -> throw BaseException(ErrorCode.USER_REFRESH_TOKEN_INVALID)
            RefreshTokenConsumeResult.Expired -> throw BaseException(ErrorCode.REFRESH_TOKEN_EXPIRED)
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
        val userId = passwordResetTokenRepository.findByIdOrThrow(request.token).userId
        val user = userRepository.findByIdOrThrow(userId)

        user.changePassword(passwordTransportResolver.resolve(request.newPassword), passwordEncoder)

        passwordResetTokenRepository.deleteById(request.token)
    }

    @Transactional
    fun register(request: RegisterRequest): UserResponse {
        if (userRepository.findByEmail(request.email) != null) throw BaseException(ErrorCode.USER_DUPLICATE_EMAIL)
        val user =
            request
                .copy(password = passwordTransportResolver.resolve(request.password))
                .toUser(passwordEncoder)
        if (userRepository.count() == 0L) user.role = UserRole.ADMIN
        return UserResponse.from(userRepository.save(user))
    }
}
