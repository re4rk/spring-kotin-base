package com.ark.base.user

import com.ark.base.auth.AuthService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserApiService(
    private val userService: UserService,
    private val authService: AuthService,
) {
    fun register(request: RegisterRequest): UserResponse =
        UserResponse.from(userService.register(request.toUserRegisterCommand()))

    @Transactional
    fun changePassword(userId: Long, request: ChangePasswordRequest) {
        authService.verifyPassword(userId, request.currentPassword)
        userService.changePassword(userId, request.newPassword)
    }

    fun delete(userId: Long) = userService.delete(userId)
}
