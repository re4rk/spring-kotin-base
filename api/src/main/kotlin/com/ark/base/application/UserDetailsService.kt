package com.ark.base.application

import com.ark.base.user.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.security.core.userdetails.UserDetailsService as SpringUserDetailsService

@Service
class UserDetailsService(
    private val userRepository: UserRepository,
) : SpringUserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username) ?: throw UsernameNotFoundException(username)
        return User(
            user.email,
            user.passwordHash ?: "",
            listOf(SimpleGrantedAuthority("ROLE_${user.role.name}")),
        )
    }
}
