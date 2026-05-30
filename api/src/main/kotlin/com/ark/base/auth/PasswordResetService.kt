package com.ark.base.auth

import com.ark.base.user.UserService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PasswordResetService(
    private val userService: UserService,
    private val tokenStore: PasswordResetTokenStore,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun request(email: String) {
        val user = userService.findByEmail(email) ?: return
        val token = UUID.randomUUID().toString()
        tokenStore.save(token, user.id)
        eventPublisher.publishEvent(PasswordResetRequestedEvent(user.email, token))
    }

    @Transactional
    fun reset(token: String, newPassword: String) {
        val userId = tokenStore.getUserId(token) ?: throw AuthException.ResetTokenInvalid()
        userService.changePassword(userId, newPassword)
        tokenStore.delete(token)
    }
}
