package com.ark.base.user

import com.ark.base.common.BaseException
import com.ark.base.common.ErrorCode
import com.ark.base.user.UserRegisteredEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun register(request: RegisterRequest): UserResponse {
        if (userRepository.findByEmail(request.email) != null) throw BaseException(ErrorCode.USER_DUPLICATE_EMAIL)
        val user =
            userRepository.save(
                User(email = request.email, name = request.name, passwordHash = passwordEncoder.encode(request.password)),
            )
        eventPublisher.publishEvent(UserRegisteredEvent(user.id, user.email, user.name))
        return UserResponse.from(user)
    }

    @Transactional(readOnly = true)
    fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    @Transactional
    fun changePassword(
        userId: Long,
        request: ChangePasswordRequest,
    ) {
        val user = userRepository.findById(userId).orElseThrow { BaseException(ErrorCode.USER_NOT_FOUND) }
        if (!passwordEncoder.matches(request.currentPassword, user.passwordHash)) throw BaseException(ErrorCode.USER_LOGIN_FAILED)
        user.changePassword(passwordEncoder.encode(request.newPassword))
    }

    @Transactional
    fun changePassword(
        userId: Long,
        newPassword: String,
    ) {
        val user = userRepository.findById(userId).orElseThrow { BaseException(ErrorCode.USER_NOT_FOUND) }
        user.changePassword(passwordEncoder.encode(newPassword))
    }

    @Transactional
    fun delete(userId: Long) {
        val user = userRepository.findById(userId).orElseThrow { BaseException(ErrorCode.USER_NOT_FOUND) }
        user.delete()
    }
}
