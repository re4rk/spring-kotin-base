package com.ark.base.user

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun register(
        email: String,
        name: String,
    ): User {
        if (userRepository.findByEmail(email) != null) throw UserException.DuplicateEmail()
        val user = userRepository.save(User(email = email, name = name))
        eventPublisher.publishEvent(UserRegisteredEvent(user.id, user.email, user.name))
        return user
    }
}
