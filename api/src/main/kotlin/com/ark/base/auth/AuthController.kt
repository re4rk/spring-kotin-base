package com.ark.base.auth

import com.ark.base.common.JwtProvider
import com.ark.base.user.UserResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val passwordResetService: PasswordResetService,
    private val jwtProvider: JwtProvider,
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): TokenResponse {
        val user = authService.login(request.toUserLoginCommand())
        return TokenResponse(accessToken = jwtProvider.generate(user.id), user = UserResponse.from(user))
    }

    @PostMapping("/password-reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun requestPasswordReset(@RequestBody request: PasswordResetRequest) =
        passwordResetService.request(request.email)

    @PostMapping("/password-reset/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun resetPassword(@RequestBody request: PasswordResetConfirmRequest) =
        passwordResetService.reset(request.token, request.newPassword)
}
