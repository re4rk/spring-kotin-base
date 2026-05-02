package com.ark.base.user

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
    fun register(command: UserRegisterCommand): User {
        if (userRepository.findByEmail(command.email) != null) throw UserException.DuplicateEmail()
        val user = userRepository.save(command.toUser(passwordEncoder))
        eventPublisher.publishEvent(UserRegisteredEvent(user.id, user.email, user.name))
        return user
    }

    @Transactional(readOnly = true)
    fun login(command: UserLoginCommand): User {
        val user = userRepository.findByEmail(command.email) ?: throw UserException.LoginFailed()
        if (!passwordEncoder.matches(command.rawPassword, user.passwordHash)) throw UserException.LoginFailed()
        return user
    }
}
