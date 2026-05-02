package com.ark.base.user

import com.ark.base.common.JwtProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserApiService(
    private val userService: UserService,
    private val jwtProvider: JwtProvider,
) {
    fun register(request: RegisterRequest): UserResponse = UserResponse.from(userService.register(request.toUserRegisterCommand()))

    fun login(request: LoginRequest): TokenResponse {
        val user = userService.login(request.toUserLoginCommand())
        return TokenResponse(accessToken = jwtProvider.generate(user.id), user = UserResponse.from(user))
    }

    @Transactional
    fun changePassword(
        userId: Long,
        request: ChangePasswordRequest,
    ) {
        userService.verifyPassword(userId, request.currentPassword)
        userService.changePassword(userId, request.newPassword)
    }
}
