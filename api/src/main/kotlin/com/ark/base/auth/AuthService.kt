package com.ark.base.auth

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.common.JwtProvider
import com.ark.base.user.PasswordEncoder
import com.ark.base.user.UserRepository
import com.ark.base.user.UserResponse
import com.ark.base.user.UserService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenStore: PasswordResetTokenRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val jwtProvider: JwtProvider,
    private val userService: UserService,
) {
    @Transactional(readOnly = true)
    fun login(request: LoginRequest): TokenResponse {
        val user = userRepository.findByEmail(request.email) ?: throw BaseException(ErrorCode.USER_LOGIN_FAILED)
        if (!passwordEncoder.matches(request.password, user.passwordHash)) throw BaseException(ErrorCode.USER_LOGIN_FAILED)
        return TokenResponse(accessToken = jwtProvider.generate(user.id), user = UserResponse.from(user))
    }

    @Transactional
    fun requestPasswordReset(request: PasswordResetRequest) {
        val user = userRepository.findByEmail(request.email) ?: return
        val token = UUID.randomUUID().toString()
        tokenStore.save(token, user.id)
        eventPublisher.publishEvent(PasswordResetRequestedEvent(user.email, token))
    }

    @Transactional
    fun resetPassword(request: PasswordResetConfirmRequest) {
        val userId = tokenStore.getUserId(request.token) ?: throw BaseException(ErrorCode.USER_RESET_TOKEN_INVALID)
        userService.changePassword(userId, request.newPassword)
        tokenStore.delete(request.token)
    }
}
