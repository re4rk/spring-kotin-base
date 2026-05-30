package com.ark.base.auth

import com.ark.base.user.PasswordEncoder
import com.ark.base.user.User
import com.ark.base.user.UserException
import com.ark.base.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional(readOnly = true)
    fun login(command: UserLoginCommand): User {
        val user = userRepository.findByEmail(command.email) ?: throw UserException.LoginFailed()
        if (!passwordEncoder.matches(command.rawPassword, user.passwordHash)) throw UserException.LoginFailed()
        return user
    }

    @Transactional(readOnly = true)
    fun verifyPassword(userId: Long, rawPassword: String) {
        val user = userRepository.findById(userId).orElseThrow { UserException.NotFound() }
        if (!passwordEncoder.matches(rawPassword, user.passwordHash)) throw UserException.LoginFailed()
    }
}
